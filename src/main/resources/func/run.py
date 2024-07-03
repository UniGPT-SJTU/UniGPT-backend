import sys
import json
from add import add  # 确保导入 add 函数

def execute_function(func_name, json_args):
    # Parse the JSON string to a Python object
    args_dict = json.loads(json_args)
    # Extract the 'params' list
    args = args_dict.get('params', [])
    
    # Retrieve the function from globals
    func = globals().get(func_name)
    if not func:
        return {"error": f"Function {func_name} not found"}
    
    try:
        # Call the function with unpacked arguments
        result = func(*args)
        return {"result": result}
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("execute_function.py <function_name> <parameters>")
        print(json.dumps({"error": "Invalid number of arguments"}))
        sys.exit(1)

    function_name = sys.argv[1]
    response = execute_function(function_name, sys.argv[2])
    print(json.dumps(response))