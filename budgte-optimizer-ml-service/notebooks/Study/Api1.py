from flask import Flask
from pymongo import MongoClient
from mongoengine import connect ,Document, StringField, IntField, FloatField,BooleanField
from mongoengine.errors import NotRegistered, ValidationError


# creamos la aplicacion 
app = Flask(__name__)
app.config['']
cliente = MongoClient('mongodb://localhost:27017/')
db = cliente['study_model']
usuario_collection = db['usuarios']
# defino una clase usuario 
class Usuarios(Document): 
    name = StringField(required=True,max_length=80)
    descripcion = StringField(required=True,max_length=120)
    edad = IntField(min_value=0, max_value=105)
    saldo = FloatField(default=0.0)
    telenfono = StringField(max_length=20)
    activo = BooleanField(default=True)
    
    

# definimos los metodos de la api 
