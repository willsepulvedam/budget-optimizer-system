import json


class BudgetPrompt:
    @staticmethod
    def _format_data(data: dict) -> str:
        return json.dumps(data, ensure_ascii=False, indent=2)

    @staticmethod
    def build_analysis_prompt(data: dict) -> str:
        return (
            "Analiza estos datos de presupuesto para mayo de 2026 y sugiere mejoras precisas."
            f" Datos:\n{BudgetPrompt._format_data(data)}"
        )

    @staticmethod
    def build_prediction_prompt(data: dict) -> str:
        return (
            "Predice el presupuesto necesario para el siguiente periodo basándote en estos datos de gasto."
            f" Datos:\n{BudgetPrompt._format_data(data)}"
        )

    @staticmethod
    def build_optimization_prompt(data: dict) -> str:
        return (
            "Optimiza este presupuesto actual y sugiere recomendaciones de ahorro y redistribución."
            f" Datos:\n{BudgetPrompt._format_data(data)}"
        )

    @staticmethod
    def build_anomaly_prompt(data: dict) -> str:
        return (
            "Detecta anomalías y patrones inusuales en estos gastos."
            f" Datos:\n{BudgetPrompt._format_data(data)}"
        )
