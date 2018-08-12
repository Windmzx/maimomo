from flask import Flask, request, render_template
import subprocess, os
import re

app = Flask(__name__)

static_folder_root = os.path.join(os.path.dirname(os.path.abspath(__file__)), "client")
app._static_folder = static_folder_root


@app.route('/')
def hello_world():
    return render_template("index.html")


@app.route('/doread', methods=['get', "POST"])
def do_read():
    url = request.form['url']
    password = request.form['password']
    # print("url:{}  password:{}".format(url, password))
    url2 = re.search("https://.*&", url)
    url2 = url2.group(0).replace("&", "")
    pid = re.search("pid=.*", url)
    pid = pid.group(0)
    # print("success")
    if password == "zzzsyzxl":

        print("start process")
        print("python attendence.py {} {}".format(url2, pid))
        p = os.popen("python attendence.py {} {}".format(url2, pid))
        # p.wait()
        # (stdoutdata, stderrdata) = p.communicate()
        res=p.read()
        return "success the result is {}".format(res)

        # return "sueecess"
    else:
        return "请联系2281927774获取密码"


if __name__ == '__main__':
    app.run(debug=True)
