# -*- coding:utf-8 -*-
import json
import sys
import importlib

def handler(event, context):
    try:
        # 解析 event 中的 body
        body = event.get("body", {})
        if isinstance(body, str):
            body = json.loads(body)
        
        module_name = body.get("module_name")
        function_name = body.get("function_name")
        params = body.get("params", [])

        if not module_name or not function_name:
            raise ValueError("module_name and function_name are required")

        # 使用 execute_function 执行指定的模块和函数
        json_args = {"params": params}
        result = execute_function(module_name, function_name, json_args)

        return {
            "statusCode": 200,
            "isBase64Encoded": False,
            "body": json.dumps(result),
            "headers": {
                "Content-Type": "application/json"
            }
        }
    except Exception as e:
        return {
            "statusCode": 500,
            "isBase64Encoded": False,
            "body": json.dumps({"error": str(e)}),
            "headers": {
                "Content-Type": "application/json"
            }
        }

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
    
    
# if __name__ == "__main__":
#     test_event = {
#         "body": json.dumps({
#             "module_name": "add",
#             "function_name": "handler",
#             "params": ["1", "2a"]
#         })
#     }
#     test_context = {}
#     response = handler(test_event, test_context)
#     print(response)