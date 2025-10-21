from flask import Flask,jsonify
import requests

app = Flask(__name__)


# creamos la ruta por defecto 
@app.route('/index', methods=['GET'])
def index(): 
    return jsonify({'texto': 'ruta principal de la api'}), 200




if __name__ == '__main__': 
    app.run(debug=True,port=5050)
