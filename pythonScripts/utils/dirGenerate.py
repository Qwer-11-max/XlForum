import os
import sys

def create_directory_structure(base_path):
    """创建指定的目录结构"""
    # 定义目录结构
    directories = [
        "api",
        "controller",
        "mapper",
        "entity",
        "service",
        os.path.join("dto", "req"),
        os.path.join("dto", "resp")
    ]

    # 创建基础目录
    try:
        os.makedirs(base_path, exist_ok=True)
        print(f"基础目录已创建: {base_path}")
    except Exception as e:
        print(f"创建基础目录失败: {e}")
        sys.exit(1)

    # 创建子目录
    for directory in directories:
        dir_path = os.path.join(base_path, directory)
        try:
            os.makedirs(dir_path, exist_ok=True)
            print(f"目录已创建: {dir_path}")
        except Exception as e:
            print(f"创建目录失败 {dir_path}: {e}")

if __name__ == "__main__":
    # 获取目标目录参数
    if len(sys.argv) != 2:
        print("用法: python create_dirs.py <目标目录>")
        sys.exit(1)

    target_dir = sys.argv[1]
    create_directory_structure(target_dir)
    print("\n目录结构创建完成！")