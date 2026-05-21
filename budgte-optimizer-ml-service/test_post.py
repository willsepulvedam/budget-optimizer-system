import urllib.request, json
url = 'http://127.0.0.1:8000/api/ml/analizar'
data = {
  "user_id": "string",
  "period": "string",
  "current_budget": 0,
  "expenses": [
    {"category": "gimansio","amount": 1000,"description": "quiero ir a el gimnasio y solo tengo 1000 pesos colombianos y vivo en cartagena es posible"}
  ],
  "goals": ["string"]
}
req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers={'Content-Type':'application/json'})
try:
    with urllib.request.urlopen(req, timeout=20) as resp:
        print('STATUS', resp.status)
        print(resp.read().decode('utf-8'))
except Exception as e:
    import traceback
    traceback.print_exc()
    print('ERR', e)
