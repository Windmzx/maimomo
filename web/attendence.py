# -*- coding: utf-8 -*-
from requests import *
import re
from termcolor import colored
import time
import threading
import sys


def getProxy(pnum):
    global proxies, num, Num
    while num < Num:
        if len(proxies) == 0:
            if B.acquire():
                # print('[+] %s' % colored('get proxy...', 'blue', attrs=['bold']), print)
                print("get proxy")
                print("</br>")

                while 1:
                    try:
                        url = 'http://www.66ip.cn/mo.php?tqsl=%s' % pnum
                        html = get(url).text
                        proxies = set(re.findall('([0-9].+)<br />', html))
                        if not len(proxies):
                            print('Waiting')
                            print("</br>")
                            time.sleep(3)
                            continue

                        # print(colored('[%d]' % len(proxies), 'yellow', attrs=['bold']),
                        #       '%s' % (colored('Done!', 'green', attrs=['bold'])))
                        print("get proxy finished")
                        print("</br>")

                        break
                    except:
                        print('  [-]Waiting')
                        time.sleep(5)
                B.release()


def autoVisit(url1):
    global num, ProxyList, proxies, Num

    url = url1
    headers = {
        'user-agent': 'Mozilla/5.0 (Linux; Android 7.0; PLUS Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.98 Mobile Safari/537.36'}

    while num < Num:
        if A.acquire():
            # print ( num
            if proxies:
                proxie = proxies.pop()
                A.release()
            else:
                A.release()
                continue

            if proxie not in ProxyList:
                try:
                    html = get(url, headers=headers, proxies={'http': proxie}, timeout=5).text
                    failed = 0
                except:
                    failed = 1

                B.acquire()
                # print(colored('[%d]' % (num), 'yellow', attrs=['bold']), )
                print(num)
                if failed:
                    # print('[%s] Failed!' % proxie)
                    print("\t\t\tFailed")
                    print("</br>")

                else:
                    num += 1
                    # print('[%s]' % colored(proxie, 'cyan', attrs=['bold']),
                    #       colored('Successfully!', 'green', attrs=['bold']))
                    print(proxie + "\t\t\tSuccessfully")
                    print("</br>")

                    ProxyList.append(proxie)
                B.release()


threads = []
proxies = []
A = threading.Lock()
B = threading.Lock()

ProxyList = []
num = 0  # already visited
pnum = 100  # proxies-num
Num = 40  # visit-num
if __name__ == '__main__':
    url = sys.argv[1]

    pid = sys.argv[2]

    url = url + "&" + pid
    print("</br>")
    print("your url is " + url)
    print("</br>")

    p = threading.Thread(target=getProxy, args=(pnum,))

    for i in range(pnum):
        t = threading.Thread(target=autoVisit, args=(url,))
        threads.append(t)

    p.start()
    for t in threads:
        t.start()

    p.join()
    for t in threads:
        t.join()

    # print('\r', ' ' * 50)
    # print('%s' % colored('[%s]' % num, 'yellow', attrs=['bold']) + colored(' All done!', 'green', attrs=['bold']))
    print("all done")
    sys.exit(0)
