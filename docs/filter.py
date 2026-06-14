import sys

with open(sys.argv[1], encoding='utf-8', errors='replace') as f:
    content = f.read()

# Fall back to latin-1 if utf-8 produced replacement characters
if '�' in content:
    with open(sys.argv[1], encoding='latin-1') as f:
        content = f.read()

sys.stdout.buffer.write(
    content.replace('com.nvivx.vixhealthsystem', 'vixhealthsystem').encode('utf-8')
)
