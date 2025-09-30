from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import uvicorn

app = FastAPI(
    title="Budget Optimizer ML Service",
    description="Servicio de Machine Learning para optimización de presupuestos",
    version="1.0.0"
)

# Configurar CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Modelos de datos
class BudgetItem(BaseModel):
    category: str
    amount: float
    month: str

class PredictionRequest(BaseModel):
    items: List[BudgetItem]
    months_ahead: int = 3

class PredictionResponse(BaseModel):
    predictions: List[dict]
    confidence: float

class OptimizationRequest(BaseModel):
    total_budget: float
    categories: List[str]
    priorities: Optional[List[int]] = None

class OptimizationResponse(BaseModel):
    optimized_allocation: dict
    savings: float

# Endpoints
@app.get("/")
async def root():
    return {
        "message": "Budget Optimizer ML Service",
        "status": "running",
        "version": "1.0.0"
    }

@app.get("/health")
async def health_check():
    return {"status": "healthy", "service": "ml-service"}

@app.post("/predict", response_model=PredictionResponse)
async def predict_budget(request: PredictionRequest):
    """
    Predice gastos futuros basado en datos históricos
    """
    try:
        # Simulación de predicción (aquí iría tu modelo ML real)
        predictions = []
        for i in range(request.months_ahead):
            month_predictions = {}
            for item in request.items:
                # Predicción simple: promedio + variación aleatoria
                predicted_amount = item.amount * (1 + (i * 0.05))
                month_predictions[item.category] = round(predicted_amount, 2)
            
            predictions.append({
                "month": f"Month +{i+1}",
                "predictions": month_predictions
            })
        
        return PredictionResponse(
            predictions=predictions,
            confidence=0.85
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/optimize", response_model=OptimizationResponse)
async def optimize_budget(request: OptimizationRequest):
    """
    Optimiza la distribución del presupuesto entre categorías
    """
    try:
        num_categories = len(request.categories)
        if num_categories == 0:
            raise HTTPException(status_code=400, detail="No categories provided")
        
        # Distribución simple si no hay prioridades
        if not request.priorities:
            amount_per_category = request.total_budget / num_categories
            optimized = {cat: round(amount_per_category, 2) for cat in request.categories}
        else:
            # Distribución basada en prioridades
            total_priority = sum(request.priorities)
            optimized = {}
            for cat, priority in zip(request.categories, request.priorities):
                allocated = (priority / total_priority) * request.total_budget
                optimized[cat] = round(allocated, 2)
        
        # Calcular "ahorro" potencial (ejemplo)
        potential_savings = request.total_budget * 0.15
        
        return OptimizationResponse(
            optimized_allocation=optimized,
            savings=round(potential_savings, 2)
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/analyze")
async def analyze_spending(items: List[BudgetItem]):
    """
    Analiza patrones de gasto
    """
    if not items:
        raise HTTPException(status_code=400, detail="No items provided")
    
    # Análisis por categoría
    category_totals = {}
    for item in items:
        if item.category in category_totals:
            category_totals[item.category] += item.amount
        else:
            category_totals[item.category] = item.amount
    
    total = sum(category_totals.values())
    
    analysis = {
        "total_spending": round(total, 2),
        "by_category": {cat: round(amt, 2) for cat, amt in category_totals.items()},
        "percentages": {cat: round((amt/total)*100, 2) for cat, amt in category_totals.items()},
        "highest_category": max(category_totals, key=category_totals.get),
        "recommendations": [
            "Consider reducing spending in the highest category",
            "Look for savings opportunities in recurring expenses"
        ]
    }
    
    return analysis

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)