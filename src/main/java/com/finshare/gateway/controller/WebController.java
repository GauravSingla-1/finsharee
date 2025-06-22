package com.finshare.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Web controller for serving the FinShare application frontend.
 */
@Controller
public class WebController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>FinShare - Group Expense Management</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 1200px; margin: 0 auto; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; text-align: center; margin-bottom: 30px; }
                    .service-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-bottom: 30px; }
                    .service-card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .service-card h3 { color: #333; margin-top: 0; }
                    .status-up { color: #4CAF50; font-weight: bold; }
                    .status-down { color: #f44336; font-weight: bold; }
                    .test-section { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
                    .test-button { background: #667eea; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; margin: 5px; }
                    .test-button:hover { background: #5a6fd8; }
                    .result { margin-top: 10px; padding: 10px; background: #f9f9f9; border-radius: 5px; font-family: monospace; white-space: pre-wrap; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>FinShare Application</h1>
                        <p>Group Expense Management with AI-Powered Categorization</p>
                    </div>
                    
                    <div class="service-grid">
                        <div class="service-card">
                            <h3>🤖 AI Service</h3>
                            <p>Status: <span class="status-up">HEALTHY</span></p>
                            <p>ML-powered expense categorization with 12 category types</p>
                        </div>
                        
                        <div class="service-card">
                            <h3>👥 Group Expense Service</h3>
                            <p>Status: <span class="status-up">UP</span></p>
                            <p>Core expense management with splitting algorithms</p>
                        </div>
                        
                        <div class="service-card">
                            <h3>⚖️ Balance Settlement Service</h3>
                            <p>Status: <span class="status-up">UP</span></p>
                            <p>Debt optimization and settlement tracking</p>
                        </div>
                        
                        <div class="service-card">
                            <h3>📊 Analytics Service</h3>
                            <p>Status: <span class="status-up">UP</span></p>
                            <p>Budget insights and spending analytics</p>
                        </div>
                        
                        <div class="service-card">
                            <h3>🔔 Notification Service</h3>
                            <p>Status: <span class="status-up">UP</span></p>
                            <p>Real-time expense notifications</p>
                        </div>
                        
                        <div class="service-card">
                            <h3>👤 User Service</h3>
                            <p>Status: <span class="status-down">CONFIGURING</span></p>
                            <p>User authentication and profile management</p>
                        </div>
                    </div>
                    
                    <div class="test-section">
                        <h3>🧪 AI Categorization Demo</h3>
                        <p>Test the AI expense categorization engine:</p>
                        <button class="test-button" onclick="testCategorization('Starbucks Coffee', 15.50)">Test Coffee Purchase</button>
                        <button class="test-button" onclick="testCategorization('Shell Gas Station', 45.00)">Test Gas Station</button>
                        <button class="test-button" onclick="testCategorization('Amazon.com', 89.99)">Test Online Shopping</button>
                        <button class="test-button" onclick="testCategorization('Whole Foods Market', 125.50)">Test Grocery Store</button>
                        <div id="test-result" class="result" style="display: none;"></div>
                    </div>
                    
                    <div class="test-section">
                        <h3>📋 Available Categories</h3>
                        <button class="test-button" onclick="getCategories()">Load Categories</button>
                        <div id="categories-result" class="result" style="display: none;"></div>
                    </div>
                </div>
                
                <script>
                    async function testCategorization(merchant, amount) {
                        const resultDiv = document.getElementById('test-result');
                        resultDiv.style.display = 'block';
                        resultDiv.textContent = 'Testing categorization...';
                        
                        try {
                            const response = await fetch('/api/ai/categorize', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json',
                                },
                                body: JSON.stringify({
                                    merchant_text: merchant,
                                    transaction_type: 'DEBIT',
                                    amount: amount
                                })
                            });
                            
                            const data = await response.json();
                            resultDiv.textContent = `Merchant: ${merchant}\\nCategory: ${data.predicted_category}\\nConfidence: ${data.confidence_score}\\nAlternatives: ${data.alternative_categories.join(', ') || 'None'}`;
                        } catch (error) {
                            resultDiv.textContent = `Error: ${error.message}`;
                        }
                    }
                    
                    async function getCategories() {
                        const resultDiv = document.getElementById('categories-result');
                        resultDiv.style.display = 'block';
                        resultDiv.textContent = 'Loading categories...';
                        
                        try {
                            const response = await fetch('/api/ai/categories');
                            const data = await response.json();
                            resultDiv.textContent = `Available Categories (${data.total_count}): \\n${data.categories.join(', ')}`;
                        } catch (error) {
                            resultDiv.textContent = `Error: ${error.message}`;
                        }
                    }
                </script>
            </body>
            </html>
            """;
    }

    @GetMapping("/app")
    @ResponseBody
    public String app() {
        return "redirect:/";
    }
}