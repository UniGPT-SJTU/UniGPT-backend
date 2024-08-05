import sys
from src import index # 函数入口文件路径，根据具体情况修改

# main方法用于调试，event是选择的调试事件
if __name__ == '__main__':
    event = { 'hello': 'world' } # 测试事件内容，根据具体情况修改
    context = ''
    content = index.handler(event, context)
    print('函数返回：')
    print(content)