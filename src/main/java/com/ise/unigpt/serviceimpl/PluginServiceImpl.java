package com.ise.unigpt.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.functiongraph.v2.FunctionGraphClient;
import com.huaweicloud.sdk.functiongraph.v2.model.ImportFunctionRequest;
import com.huaweicloud.sdk.functiongraph.v2.model.ImportFunctionRequestBody;
import com.huaweicloud.sdk.functiongraph.v2.model.ImportFunctionResponse;
import com.huaweicloud.sdk.functiongraph.v2.region.FunctionGraphRegion;
import com.ise.unigpt.dto.GetPluginsOkResponseDTO;
import com.ise.unigpt.dto.PluginBriefInfoDTO;
import com.ise.unigpt.dto.PluginCreateDTO;
import com.ise.unigpt.dto.PluginCreateTestDTO;
import com.ise.unigpt.dto.PluginDetailInfoDTO;
import com.ise.unigpt.dto.PluginEditInfoDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.Plugin;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.PluginRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.DockerService;
import com.ise.unigpt.service.PluginService;
import com.ise.unigpt.utils.PaginationUtils;

@Service
public class PluginServiceImpl implements PluginService {

    private final PluginRepository pluginRepository;
    private final AuthService authService;
    private final DockerService dockerService;

    public PluginServiceImpl(PluginRepository pluginRepository, AuthService authService, DockerService dockerService) {
        this.pluginRepository = pluginRepository;
        this.authService = authService;
        this.dockerService = dockerService;
    }

    @Override
    public GetPluginsOkResponseDTO getPlugins(String q, String order, Integer page, Integer pageSize) {
        List<PluginBriefInfoDTO> plugins;
        if (order.equals("latest")) {
            plugins = pluginRepository.findAllByOrderByIdDesc()
                    .stream()
                    .filter(plugin -> q.isEmpty() || plugin.getName().contains(q))
                    .filter(plugin -> plugin.getIsPublished())
                    .map(plugin -> new PluginBriefInfoDTO(plugin.getId(), plugin.getName(), plugin.getDescription(), plugin.getAvatar(),
                    false, false))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid order parameter");
        }

        return new GetPluginsOkResponseDTO(plugins.size(), PaginationUtils.paginate(plugins, page, pageSize));
    }

    @Override
    public PluginDetailInfoDTO getPluginInfo(Integer id, String token) {

        Plugin plugin = pluginRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plugin not found for ID: " + id));

        User user = authService.getUserByToken(token);

        if (!plugin.getIsPublished() && plugin.getCreator() != user) {
            // 如果plugin未发布且请求用户不是plugin的创建者，则抛出异常
            throw new NoSuchElementException("Plugin not published for ID: " + id);
        }
        return new PluginDetailInfoDTO(plugin, user);
    }

    @Override
    public PluginEditInfoDTO getPluginEditInfo(Integer id, String token) {
        Plugin plugin = pluginRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plugin not found for ID: " + id));

        User user = authService.getUserByToken(token);

        if (plugin.getCreator() != user) {
            // 如果请求用户不是plugin的创建者，则抛出异常
            throw new NoSuchElementException("Permission denied for ID: " + id);
        }
        return new PluginEditInfoDTO(plugin, user);
    }

    @Override
    public ResponseDTO createPlugin(PluginCreateDTO dto, String token) throws Exception {
        User user = authService.getUserByToken(token);

        // 构建目标文件路径
        String directoryPath = "src/main/resources/" + user.getAccount() + "/" + dto.getName();
        String filePath = directoryPath + "/" + dto.getName() + ".py";

        // 判断文件是否存在，如果存在则抛出异常
        if (Files.exists(Paths.get(filePath))) {
            return new ResponseDTO(false, "Plugin already exists");
        }

        // 创建目录
        Path path = Paths.get(directoryPath);
        Files.createDirectories(path);

        // 将code字段的内容写入到文件中
        Path file = Paths.get(filePath);
        Files.writeString(file, dto.getCode(), StandardOpenOption.CREATE);

        // 拷贝index.py文件，index.py文件在src/main/resources/common/index.py
        Path indexFile = Paths.get("src/main/resources/common/index.py");
        Path targetIndexFile = Paths.get(directoryPath + "/index.py");
        Files.copy(indexFile, targetIndexFile);

        // 将index.py和name.py文件打包成zip文件
        String zipFileName = "index" + ".zip";
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipFileName)))) {
            addToZipFile(new File(filePath), zos);
            addToZipFile(new File(directoryPath + "/index.py"), zos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 删除name.py文件和index.py文件
        Files.delete(Paths.get(filePath));
        Files.delete(Paths.get(directoryPath + "/index.py"));

        // 拷贝yaml文件  yaml文件在src/main/resources/common/common.yaml， 拷贝后命名为DTO的name字段
        Path yamlFile = Paths.get("src/main/resources/common/common.yaml");
        Path targetYamlFile = Paths.get(directoryPath + "/" + dto.getName() + ".yaml");
        Files.copy(yamlFile, targetYamlFile);

        // 将yaml文件和index.zip文件打包成zip文件
        String zipFileName2 = dto.getName() + ".zip";
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipFileName2)))) {
            addToZipFile(new File(targetYamlFile.toString()), zos);
            addToZipFile(new File(zipFileName), zos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 删除index.zip文件和yaml文件
        Files.delete(Paths.get(zipFileName));
        Files.delete(Paths.get(targetYamlFile.toString()));

        String functionName = user.getAccount() + "_" + dto.getName();

        // 上传到华为云
        try {
            String urn = uploadFunction(zipFileName2, functionName);
            // 删除zip文件
            Plugin plugin = new Plugin(dto, user, "", urn);
            pluginRepository.save(plugin);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(false, "Upload function failed");
        }

        return new ResponseDTO(true, "Create plugin successfully");
    }

    @Override
    public ResponseDTO testCreatePlugin(PluginCreateTestDTO dto, String token) throws Exception {
        User user = authService.getUserByToken(token);

        // 构建目标文件路径
        String directoryPath = "src/main/resources/test/" + user.getAccount();
        String filePath = directoryPath + "/" + dto.getName() + ".py";

        // 判断文件是否存在，如果存在则清除
        if (Files.exists(Paths.get(filePath))) {
            Files.delete(Paths.get(filePath));
        }

        // 创建目录
        Path path = Paths.get(directoryPath);
        Files.createDirectories(path);

        // 将code字段的内容写入到文件中
        Path file = Paths.get(filePath);
        Files.writeString(file, dto.getCode(), StandardOpenOption.CREATE);

        // 调用dockerService执行测试
        String output = dockerService.invokeFunction("test/" + user.getAccount(), dto.getName(), "handler", dto.getParamsValue());

        // 解析output为JSONObject来检查是否有error字段
        JSONObject jsonResponse = new JSONObject(output);
        boolean isSuccess = !jsonResponse.has("error");

        // 删除测试文件
        Files.delete(Paths.get(filePath));

        return new ResponseDTO(isSuccess, output);
    }

    public String uploadFunction(String fileName, String functionName) throws IOException {

        // zip 是根目录下test_latest.zip
        String zipFileName = fileName;
        File zipFile = new File(zipFileName);

        // Read ZIP file content
        byte[] zipFileContent = Files.readAllBytes(zipFile.toPath());

        // Create request body
        ImportFunctionRequestBody body = new ImportFunctionRequestBody()
                .withFileCode(Base64.getEncoder().encodeToString(zipFileContent))
                .withFileType("zip")
                .withFileName(zipFileName)
                .withFuncName(functionName);

        String ak = System.getenv("HUAWEICLOUD_SDK_AK");
        String sk = System.getenv("HUAWEICLOUD_SDK_SK");

        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);

        FunctionGraphClient client = FunctionGraphClient.newBuilder()
                .withCredential(auth)
                .withRegion(FunctionGraphRegion.valueOf("cn-east-3"))
                .build();
        ImportFunctionRequest request = new ImportFunctionRequest();

        request.withBody(body);

        ImportFunctionResponse response = client.importFunction(request);
        System.out.println(response.toString());
        // 返回函数的URN
        return response.getFuncUrn();
    }

    private void addToZipFile(File file, ZipOutputStream zos) throws IOException {
        try (var fis = Files.newInputStream(file.toPath())) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.closeEntry();
        }
    }
}
