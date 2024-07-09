package com.ise.unigpt.serviceimpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ise.unigpt.dto.GetPluginsOkResponseDTO;
import com.ise.unigpt.dto.PluginBriefInfoDTO;
import com.ise.unigpt.dto.PluginCreateDTO;
import com.ise.unigpt.dto.PluginDetailInfoDTO;
import com.ise.unigpt.dto.PluginEditInfoDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.Plugin;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.PluginRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.PluginService;
import com.ise.unigpt.utils.PaginationUtils;

@Service
public class PluginServiceImpl implements PluginService {

    private final PluginRepository pluginRepository;
    private final AuthService authService;

    public PluginServiceImpl(PluginRepository pluginRepository, AuthService authService) {
        this.pluginRepository = pluginRepository;
        this.authService = authService;
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
        String directoryPath = "src/main/resources/" + user.getAccount();
        String filePath = directoryPath + "/" + dto.getName() + ".py";

        // 创建目录
        Path path = Paths.get(directoryPath);
        Files.createDirectories(path);

        // 将code字段的内容写入到文件中
        Path file = Paths.get(filePath);
        Files.writeString(file, dto.getCode(), StandardOpenOption.CREATE);

        Plugin plugin = new Plugin(dto, user, filePath);
        pluginRepository.save(plugin);
        return new ResponseDTO(true, "Create plugin successfully");
    }
}
