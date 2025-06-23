#!/usr/bin/env python3
"""
CORS Proxy for FinShare Dashboard
Handles cross-origin requests to all microservices
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
import json

app = Flask(__name__)
CORS(app)

# Service endpoints
SERVICES = {
    'auth': 'http://127.0.0.1:5000',
    'ai': 'http://127.0.0.1:8004',
    'group': 'http://127.0.0.1:8002',
    'balance': 'http://127.0.0.1:8003',
    'analytics': 'http://127.0.0.1:8005',
    'notification': 'http://127.0.0.1:8006'
}

@app.route('/proxy/<service>/<path:endpoint>', methods=['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'])
def proxy_request(service, endpoint):
    if service not in SERVICES:
        return jsonify({'error': 'Unknown service'}), 400
    
    target_url = f"{SERVICES[service]}/{endpoint}"
    
    # Prepare headers
    headers = {}
    for key, value in request.headers:
        if key.lower() not in ['host', 'content-length']:
            headers[key] = value
    
    try:
        # Forward the request
        if request.method == 'GET':
            response = requests.get(target_url, headers=headers, params=request.args)
        elif request.method == 'POST':
            response = requests.post(target_url, headers=headers, data=request.get_data(), params=request.args)
        elif request.method == 'PUT':
            response = requests.put(target_url, headers=headers, data=request.get_data(), params=request.args)
        elif request.method == 'DELETE':
            response = requests.delete(target_url, headers=headers, params=request.args)
        else:
            return jsonify({'error': 'Method not allowed'}), 405
        
        # Return response
        try:
            return jsonify(response.json()), response.status_code
        except:
            return response.text, response.status_code
            
    except requests.exceptions.RequestException as e:
        return jsonify({'error': str(e), 'service_offline': True}), 503

@app.route('/health')
def health():
    return jsonify({'status': 'ok', 'services': SERVICES})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=3001, debug=True)