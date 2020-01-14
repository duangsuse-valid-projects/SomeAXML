from struct import pack

def file_content():
  return pack('biL', 0x7F, 0x77BBCCDD, 0xCAFEBABE) + pack('>iL', 0x77FFEEDD, 0xCAFEBABE)

with open("binary", 'wb') as f:
  f.write(file_content())
