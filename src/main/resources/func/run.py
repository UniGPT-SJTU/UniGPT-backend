import sys
import json
import importlib

def execute_function(module_name, func_name, json_args):
    try:
        # 动态导入模块
        module = importlib.import_module(module_name)
        # 动态获取函数
        func = getattr(module, func_name)
        # 调用函数并获得结果
        args = json_args.get('params', [])
        result = func(*args)
        return {"result": result}
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: run.py <module_name> <function_name> <parameters>")
        print(json.dumps({"error": "Invalid number of arguments"}))
        sys.exit(1)

    module_name = sys.argv[1]
    function_name = sys.argv[2]
    parameters = json.loads(sys.argv[3])
    response = execute_function(module_name, function_name, parameters)
    print(json.dumps(response))