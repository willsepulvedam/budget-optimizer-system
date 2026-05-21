from fastapi import HTTPException
from app.providers.gemini_provider import get_provider
from app.prompts.budget_prompt import BudgetPrompt
from app.schemas.request.budget_request import BudgetRequest
from app.schemas.response.budget_response import BudgetResponse

class BudgetService:
    def __init__(self):
        # use cached provider instance
        self.provider = get_provider()

    def analyze(self, request: BudgetRequest) -> BudgetResponse:
        prompt = self._build_analysis_prompt(request)
        result = self.provider.call_model(prompt)
        if result.get("summary") == "error calling model" or (result.get("details") and result.get("details").get("error")):
            raise HTTPException(status_code=502, detail=result)
        return BudgetResponse(
            summary=result.get("summary", "Análisis completado"),
            recommended_budget=result.get("recommended_budget", request.current_budget),
            risk_level=result.get("risk_level", "moderado"),
            details=result.get("details", {}),
        )

    def predict(self, request: BudgetRequest) -> BudgetResponse:
        prompt = self._build_prediction_prompt(request)
        result = self.provider.call_model(prompt)
        if result.get("summary") == "error calling model" or (result.get("details") and result.get("details").get("error")):
            raise HTTPException(status_code=502, detail=result)
        return BudgetResponse(
            summary=result.get("summary", "Predicción de presupuesto"),
            recommended_budget=result.get("predicted_budget", request.current_budget),
            risk_level=result.get("risk_level", "moderado"),
            details=result.get("details", {}),
        )

    def optimize(self, request: BudgetRequest) -> BudgetResponse:
        prompt = self._build_optimization_prompt(request)
        result = self.provider.call_model(prompt)
        if result.get("summary") == "error calling model" or (result.get("details") and result.get("details").get("error")):
            raise HTTPException(status_code=502, detail=result)
        return BudgetResponse(
            summary=result.get("summary", "Optimización de presupuesto"),
            recommended_budget=result.get("optimized_budget", request.current_budget),
            risk_level=result.get("risk_level", "bajo"),
            details=result.get("details", {}),
        )

    def detect_anomalies(self, request: BudgetRequest) -> BudgetResponse:
        prompt = self._build_anomaly_prompt(request)
        result = self.provider.call_model(prompt)
        if result.get("summary") == "error calling model" or (result.get("details") and result.get("details").get("error")):
            raise HTTPException(status_code=502, detail=result)
        return BudgetResponse(
            summary=result.get("summary", "Detección de anomalías"),
            detected_anomalies=result.get("anomalies", []),
            details=result.get("details", {}),
        )

    def _build_analysis_prompt(self, request: BudgetRequest) -> str:
        return BudgetPrompt.build_analysis_prompt(request.model_dump())

    def _build_prediction_prompt(self, request: BudgetRequest) -> str:
        return BudgetPrompt.build_prediction_prompt(request.model_dump())

    def _build_optimization_prompt(self, request: BudgetRequest) -> str:
        return BudgetPrompt.build_optimization_prompt(request.model_dump())

    def _build_anomaly_prompt(self, request: BudgetRequest) -> str:
        return BudgetPrompt.build_anomaly_prompt(request.model_dump())
