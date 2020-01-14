from struct import pack

def file_content():
  xs = [0x7F, 0x77BBCCDD, 0x7AFEBABE]
  return pack('biL', *xs) + pack('>bixxxxL', *xs)

with open("binary", 'wb') as f:
  f.write(file_content())
