import random
list  = ['s1','s2','s3','s4','s5', 's6', 's7', 's8', 's9', 's10', 's11', 's12']


with open('leader-12.csv', 'w') as file:
    file.write('sensor-id,humidity\n')
    for i in range(1000):
      print(i)
      valornan = [str(random.randint(0,101)), 'NaN']
      file.write(random.choice(list) + "," + str(random.randint(0,101)) + "\n")
