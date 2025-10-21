from flask import Flask, jsonify, request
from mongoengine import connect ,Document, StringField, IntField, FloatField,BooleanField
from mongoengine.errors import NotRegistered, ValidationError, DoesNotExist
import google.generativeai as gem 

gem.configure(api_key='api-tuya') # type: ignore
model = gem.GenerativeModel('gemini-pro') # type: ignore

app = Flask(__name__)

connect('study_model', host='mongodb://localhost:27017/study_model')

class Usuarios(Document): 
    nombre = StringField(required=True,max_length=80)
    descripcion = StringField(required=True,max_length=400)
    edad = IntField(min_value=0, max_value=105)
    saldo = FloatField(default=0.0)
    telenfono = StringField(max_length=20)
    activo = BooleanField(default=True)
    
    meta = {'collection': 'usuarios'}

@app.route('/')
def index(): 
    return jsonify('Bienvenido a la api de prueba caremonda')

@app.route('/formulario')
def formulario():
    return '''
    <!DOCTYPE html>
    <html>
    <head>
        <title>Agregar Usuario</title>
        <style>
            body { font-family: Arial; max-width: 500px; margin: 50px auto; padding: 20px; }
            input, button { width: 100%; padding: 10px; margin: 10px 0; }
            button { background: #007bff; color: white; border: none; cursor: pointer; }
            button:hover { background: #0056b3; }
            .resultado { padding: 15px; margin-top: 20px; border-radius: 5px; }
            .exito { background: #d4edda; color: #155724; }
            .error { background: #f8d7da; color: #721c24; }
        </style>
    </head>
    <body>
        <h2>Agregar Nuevo Usuario</h2>
        <form id="formUsuario">
            <input type="text" id="nombre" placeholder="Nombre" required>
            <input type="text" id="descripcion" placeholder="Descripción" required>
            <input type="number" id="edad" placeholder="Edad" min="0" max="105" required>
            <input type="number" step="0.01" id="saldo" placeholder="Saldo" value="0">
            <input type="text" id="telenfono" placeholder="Teléfono">
            <label>
                <input type="checkbox" id="activo" checked> Activo
            </label>
            <button type="submit">Guardar Usuario</button>
        </form>
        <div id="resultado"></div>
        <br>
        <a href="/analisis" style="display: block; text-align: center; padding: 15px; background: #28a745; color: white; text-decoration: none; border-radius: 5px;">
            🤖 Ver Análisis con IA
        </a>

        <script>
            document.getElementById('formUsuario').addEventListener('submit', async (e) => {
                e.preventDefault();
                
                const datos = {
                    nombre: document.getElementById('nombre').value,
                    descripcion: document.getElementById('descripcion').value,
                    edad: parseInt(document.getElementById('edad').value),
                    saldo: parseFloat(document.getElementById('saldo').value),
                    telenfono: document.getElementById('telenfono').value,
                    activo: document.getElementById('activo').checked
                };

                try {
                    const response = await fetch('/usuarios', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(datos)
                    });

                    const resultado = await response.json();
                    const div = document.getElementById('resultado');
                    
                    if (response.ok) {
                        div.className = 'resultado exito';
                        div.innerHTML = '✓ ' + resultado.mensaje;
                        document.getElementById('formUsuario').reset();
                        document.getElementById('activo').checked = true;
                    } else {
                        div.className = 'resultado error';
                        div.innerHTML = '✗ Error: ' + JSON.stringify(resultado);
                    }
                } catch (error) {
                    document.getElementById('resultado').className = 'resultado error';
                    document.getElementById('resultado').innerHTML = '✗ Error: ' + error;
                }
            });
        </script>
    </body>
    </html>
    '''
    
@app.route('/analisis')
def analisi(): 
    return '''
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Análisis de Usuarios con IA</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .header {
            background: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            text-align: center;
        }
        
        h1 {
            color: #667eea;
            margin-bottom: 10px;
        }
        
        .btn-principal {
            display: inline-block;
            padding: 12px 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 8px;
            text-decoration: none;
            margin: 10px;
            font-weight: bold;
            border: none;
            cursor: pointer;
        }
        
        .btn-principal:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .analisis-general {
            background: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }
        
        .analisis-general h2 {
            color: #667eea;
            margin-bottom: 15px;
        }
        
        .analisis-content {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
            white-space: pre-wrap;
            line-height: 1.6;
        }
        
        .usuarios-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .usuario-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            transition: transform 0.3s;
        }
        
        .usuario-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.3);
        }
        
        .usuario-header {
            border-bottom: 2px solid #667eea;
            padding-bottom: 15px;
            margin-bottom: 15px;
        }
        
        .usuario-nombre {
            color: #667eea;
            font-size: 22px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .usuario-descripcion {
            color: #666;
            font-style: italic;
            margin-bottom: 15px;
        }
        
        .usuario-info {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 10px;
            margin-bottom: 15px;
            font-size: 14px;
        }
        
        .info-item {
            background: #f8f9fa;
            padding: 8px;
            border-radius: 5px;
        }
        
        .btn-analizar {
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #00b09b 0%, #96c93d 100%);
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: bold;
            font-size: 14px;
        }
        
        .btn-analizar:hover {
            opacity: 0.9;
        }
        
        .btn-analizar:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
        
        .analisis-ia {
            margin-top: 15px;
            padding: 15px;
            background: #e3f2fd;
            border-radius: 8px;
            border-left: 4px solid #2196f3;
            display: none;
        }
        
        .analisis-ia.show {
            display: block;
            animation: slideIn 0.3s ease;
        }
        
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .analisis-ia h4 {
            color: #2196f3;
            margin-bottom: 10px;
        }
        
        .analisis-texto {
            white-space: pre-wrap;
            line-height: 1.6;
            color: #333;
        }
        
        .loading {
            text-align: center;
            padding: 40px;
            color: #667eea;
            font-size: 18px;
        }
        
        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 8px;
            margin: 20px 0;
        }
        
        .badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }
        
        .badge-activo {
            background: #d4edda;
            color: #155724;
        }
        
        .badge-inactivo {
            background: #f8d7da;
            color: #721c24;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🤖 Análisis de Usuarios con IA</h1>
            <p style="color: #666; margin-top: 10px;">Gemini analiza cada usuario basándose en su nombre y descripción</p>
            <a href="/formulario" class="btn-principal">← Volver al formulario</a>
            <button onclick="analizarTodos()" class="btn-principal" id="btnAnalizarTodos">🔍 Análisis General</button>
        </div>
        
        <div class="analisis-general" id="analisisGeneral" style="display: none;">
            <h2>📊 Análisis General de la Base de Datos</h2>
            <div class="analisis-content" id="analisisGeneralContent">
                Cargando análisis...
            </div>
        </div>
        
        <div id="usuariosContainer">
            <div class="loading">⏳ Cargando usuarios...</div>
        </div>
    </div>

    <script>
        window.onload = () => {
            cargarUsuarios();
        };
        
        async function cargarUsuarios() {
            try {
                const response = await fetch('/usuarios');
                const usuarios = await response.json();
                
                const container = document.getElementById('usuariosContainer');
                
                if (usuarios.length === 0) {
                    container.innerHTML = '<div class="error">No hay usuarios registrados. <a href="/formulario">Agregar uno ahora</a></div>';
                    return;
                }
                
                container.innerHTML = '<div class="usuarios-grid"></div>';
                const grid = container.querySelector('.usuarios-grid');
                
                usuarios.forEach(usuario => {
                    const card = crearCardUsuario(usuario);
                    grid.appendChild(card);
                });
                
            } catch (error) {
                document.getElementById('usuariosContainer').innerHTML = 
                    '<div class="error">Error al cargar usuarios: ' + error.message + '</div>';
            }
        }
        
        function crearCardUsuario(usuario) {
            const card = document.createElement('div');
            card.className = 'usuario-card';
            
            const nombreEscapado = usuario.nombre.replace(/'/g, "\\\\'").replace(/"/g, '\\\\"');
            const descripcionEscapada = usuario.descripcion.replace(/'/g, "\\\\'").replace(/"/g, '\\\\"');
            
            card.innerHTML = `
                <div class="usuario-header">
                    <div class="usuario-nombre">${usuario.nombre}</div>
                    <span class="badge ${usuario.activo ? 'badge-activo' : 'badge-inactivo'}">
                        ${usuario.activo ? '✓ Activo' : '✗ Inactivo'}
                    </span>
                </div>
                
                <div class="usuario-descripcion">"${usuario.descripcion}"</div>
                
                <div class="usuario-info">
                    <div class="info-item">
                        <strong>Edad:</strong> ${usuario.edad} años
                    </div>
                    <div class="info-item">
                        <strong>Saldo:</strong> $${usuario.saldo.toFixed(2)}
                    </div>
                    <div class="info-item">
                        <strong>Teléfono:</strong> ${usuario.telenfono || 'N/A'}
                    </div>
                    <div class="info-item">
                        <strong>ID:</strong> ${usuario._id.substring(0, 8)}...
                    </div>
                </div>
                
                <button class="btn-analizar" data-id="${usuario._id}" data-nombre="${nombreEscapado}" data-descripcion="${descripcionEscapada}" data-edad="${usuario.edad}" data-saldo="${usuario.saldo}" data-activo="${usuario.activo}">
                    🤖 Analizar con IA
                </button>
                
                <div class="analisis-ia" id="analisis-${usuario._id}">
                    <h4>🧠 Análisis de IA:</h4>
                    <div class="analisis-texto" id="texto-${usuario._id}"></div>
                </div>
            `;
            
            card.querySelector('.btn-analizar').addEventListener('click', function() {
                analizarUsuario(this);
            });
            
            return card;
        }
        
        async function analizarUsuario(btn) {
            const id = btn.dataset.id;
            const nombre = btn.dataset.nombre;
            const descripcion = btn.dataset.descripcion;
            const edad = parseInt(btn.dataset.edad);
            const saldo = parseFloat(btn.dataset.saldo);
            const activo = btn.dataset.activo === 'true';
            
            const analisisDiv = document.getElementById('analisis-' + id);
            const textoDiv = document.getElementById('texto-' + id);
            
            btn.disabled = true;
            btn.textContent = '⏳ Analizando...';
            
            try {
                const response = await fetch('/usuarios/ia/analizar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        nombre: nombre,
                        descripcion: descripcion,
                        edad: edad,
                        saldo: saldo,
                        activo: activo
                    })
                });
                
                const resultado = await response.json();
                
                if (response.ok) {
                    textoDiv.textContent = resultado.analisis;
                    analisisDiv.classList.add('show');
                    btn.textContent = '✓ Analizado';
                    btn.style.background = 'linear-gradient(135deg, #28a745 0%, #20c997 100%)';
                } else {
                    textoDiv.textContent = 'Error: ' + (resultado.error || 'Error desconocido');
                    analisisDiv.classList.add('show');
                    btn.disabled = false;
                    btn.textContent = '🤖 Analizar con IA';
                }
                
            } catch (error) {
                textoDiv.textContent = 'Error de conexión: ' + error.message;
                analisisDiv.classList.add('show');
                btn.disabled = false;
                btn.textContent = '🤖 Analizar con IA';
            }
        }
        
        async function analizarTodos() {
            const btn = document.getElementById('btnAnalizarTodos');
            const analisisDiv = document.getElementById('analisisGeneral');
            const contentDiv = document.getElementById('analisisGeneralContent');
            
            btn.disabled = true;
            btn.textContent = '⏳ Analizando...';
            analisisDiv.style.display = 'block';
            contentDiv.textContent = 'Analizando todos los usuarios...';
            
            try {
                const responseUsuarios = await fetch('/usuarios');
                const usuarios = await responseUsuarios.json();
                
                let resumenUsuarios = 'Análisis general de ' + usuarios.length + ' usuarios:\\n\\n';
                usuarios.forEach((u, index) => {
                    resumenUsuarios += `${index + 1}. ${u.nombre} - ${u.descripcion} (Saldo: $${u.saldo}, Edad: ${u.edad})\\n`;
                });
                
                const response = await fetch('/usuarios/ia/analizar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        nombre: 'Análisis General de Base de Datos',
                        descripcion: resumenUsuarios + '\\n\\nPor favor, proporciona un análisis general de estos usuarios, identificando patrones, perfiles comunes y recomendaciones generales para la ciudad de Cartagena.',
                        saldo: 0
                    })
                });
                
                const resultado = await response.json();
                
                if (response.ok) {
                    contentDiv.textContent = resultado.analisis;
                    btn.textContent = '✓ Análisis Completado';
                    btn.style.background = 'linear-gradient(135deg, #28a745 0%, #20c997 100%)';
                } else {
                    contentDiv.textContent = 'Error: ' + (resultado.error || 'Error desconocido');
                    btn.disabled = false;
                    btn.textContent = '🔍 Análisis General';
                }
                
            } catch (error) {
                contentDiv.textContent = 'Error de conexión: ' + error.message;
                btn.disabled = false;
                btn.textContent = '🔍 Análisis General';
            }
        }
    </script>
</body>
</html>
'''

@app.route('/usuarios/ia/analizar', methods=['POST'])
def analizar_descripcion():
    try:
        data = request.get_json()
        print(f"Datos recibidos: {data}")
        
        if not data:
            return jsonify({'error': 'No se recibieron datos'}), 400
        
        nombre = data.get('nombre')
        descripcion = data.get('descripcion')
        saldo = data.get('saldo', 0.0)
        
        if not nombre or not descripcion:
            return jsonify({'error': 'Nombre y descripción son requeridos'}), 400
        
        prompt = f'Eres un analista y recomendador de mejores sitios para hacer compras y adquirir servicios. La persona {nombre} quiere {descripcion} pero tiene un presupuesto de ${saldo}. Recomiéndale los mejores sitios para realizar su inversión en la ciudad de Cartagena.'
        
        print(f"Enviando prompt a Gemini...")
        response = model.generate_content(prompt)
        print(f"Respuesta recibida de Gemini")
        
        return jsonify({
            'analisis': response.text.strip(),
            'usuario': nombre
        }), 200
        
    except Exception as e:
        print(f"Error en análisis IA: {str(e)}")
        import traceback
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

@app.route('/usuarios', methods=['POST'])
def crear_usuario(): 
    try: 
        data = request.get_json()
        
        nuevo_usuario = Usuarios(
            nombre = data.get('nombre'),
            descripcion = data.get('descripcion'),
            edad = data.get('edad'),
            saldo = data.get('saldo', 0.0),
            telenfono = data.get('telenfono'),
            activo = data.get('activo',True)
        )
        
        nuevo_usuario.save()
        
        return jsonify({
            'mensaje': f'Usuario {nuevo_usuario.nombre} guardado exitosamente', 
            'id': str(nuevo_usuario.pk)
        }), 201
        
    except ValidationError as e: 
        return jsonify({'error': f'Error de validacion: {str(e)}'}), 400
    except Exception as e: 
        return jsonify({'error': str(e)}), 500
    
@app.route('/usuarios', methods=['GET'])
def get_all_usuarios():
    try:
        usuarios = Usuarios.objects() # type: ignore
        lista_usuarios = []
        for u in usuarios:
            usuario_dict = u.to_mongo().to_dict()
            usuario_dict['_id'] = str(usuario_dict['_id'])
            lista_usuarios.append(usuario_dict)
        return jsonify(lista_usuarios)
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
@app.route('/usuarios/<string:usuario_id>', methods=['GET'])
def get_usuario(usuario_id: str):
    try:
        usuario = Usuarios.objects(id=usuario_id).first() # type: ignore
        if usuario:
            usuario_dict = usuario.to_mongo().to_dict()
            usuario_dict['_id'] = str(usuario_dict['_id'])
            return jsonify(usuario_dict)
        else:
            return jsonify({"error": "Usuario no encontrado"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    
if __name__ == '__main__': 
    app.run(debug=True)