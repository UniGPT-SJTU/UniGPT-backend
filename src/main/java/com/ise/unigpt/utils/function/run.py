import sys
import json

def execute_function(func_name, args):
    func = globals().get(func_name)
    if not func:
        return {"error": f"Function {func_name} not found"}
    try:
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
    parameters = json.loads(sys.argv[2])
    response = execute_function(function_name, parameters)
    print(json.dumps(response))