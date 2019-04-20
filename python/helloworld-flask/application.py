import os
from flask import render_template, Flask

app=Flask(__name__)

@app.route('/')
@app.route('/<name>')
def hello(name=None):
    print("Request: {}".format(name))
    return render_template('hello.html', name=name)

if __name__ == '__main__':
    app.run(port=int(os.environ.get('PORT', '8000')), host='0.0.0.0')
