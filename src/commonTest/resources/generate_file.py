from struct import pack

def file_content():
  xs = [0x7F, 0x77BBCCDD, 0xCAFEBABE]
  return pack('biL', *xs) + pack('>biL', *xs)

with open("binary", 'wb') as f:
  f.write(file_content())
