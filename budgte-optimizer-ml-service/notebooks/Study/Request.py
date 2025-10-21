import requests


def obtener_respuesta(): 
    respuesta = requests.get(' http://127.0.0.1:5050/index')
    print(respuesta.status_code)
    print(f'Respuesta: {respuesta.json()}')



obtener_respuesta()