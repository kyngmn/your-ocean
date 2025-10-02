import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import numpy as np
from typing import Dict, List, Tuple

class BERTEmotionDetector:
    """BERT 기반 감정 분석 모델"""
    
    # 사용 가능한 모델들 (Microsoft 모델을 기본으로)
    AVAILABLE_MODELS = {
        "Nasserelsaman/microsoft-finetuned-personality": {
            "name": "Microsoft Finetuned Personality", 
            "description": "Microsoft에서 파인튜닝한 고성능 성격 분석 모델 (추천)",
            "labels": ["친화성", "성실성", "외향성", "신경성", "개방성"]
        },
        "Minej/bert-base-personality": {
            "name": "Minej BERT Personality",
            "description": "BERT 기반 성격 분석 모델",
            "labels": ["친화성", "성실성", "외향성", "신경성", "개방성"]
        }
    }
    
    def __init__(self, model_name: str = "Nasserelsaman/microsoft-finetuned-personality"):
        """
        BERT 감정 분석 모델 초기화
        
        Args:
            model_name: HuggingFace 모델 이름
        """
        self.model_name = model_name
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        
        print(f"🔥 디바이스: {self.device}")
        print(f"📦 모델 로딩 중: {model_name}")
        
        # 토크나이저와 모델 로드
        self.tokenizer = AutoTokenizer.from_pretrained(model_name)
        self.model = AutoModelForSequenceClassification.from_pretrained(model_name)
        self.model.to(self.device)
        self.model.eval()
        
        # 모델별 레이블 설정
        if model_name in self.AVAILABLE_MODELS:
            self.emotion_labels = self.AVAILABLE_MODELS[model_name]["labels"]
        else:
            self.emotion_labels = ["기쁨", "슬픔", "분노", "두려움", "놀라움", "혐오", "중립"]
        
        print("✅ 모델 로딩 완료!")
    
    @classmethod
    def get_available_models(cls):
        """사용 가능한 모델 목록 반환"""
        return cls.AVAILABLE_MODELS
    
    def predict_emotion(self, text: str) -> Dict[str, float]:
        """
        텍스트의 감정을 예측합니다.
        
        Args:
            text: 분석할 텍스트
            
        Returns:
            감정별 확률 딕셔너리
        """
        # 토크나이징
        inputs = self.tokenizer(
            text,
            return_tensors="pt",
            max_length=512,
            truncation=True,
            padding=True
        )
        
        # GPU로 이동
        inputs = {key: value.to(self.device) for key, value in inputs.items()}
        
        # 예측
        with torch.no_grad():
            outputs = self.model(**inputs)
            predictions = torch.nn.functional.softmax(outputs.logits, dim=-1)
        
        # 결과 변환
        probabilities = predictions.cpu().numpy()[0]
        
        # 모델별 라벨 매핑 처리
        num_classes = len(probabilities)
        
        # Big5 성격 모델의 경우 모델별 라벨 순서에 맞게 매핑
        if num_classes == 5:
            # Microsoft 모델: LABEL_0=Agreeableness, LABEL_1=Conscientiousness, LABEL_2=Extraversion, LABEL_3=Neuroticism, LABEL_4=Openness
            if "microsoft" in self.model_name.lower():
                labels = ['친화성', '성실성', '외향성', '신경성', '개방성']
            else:
                # 다른 모델들의 기본 순서 (Minej 모델 등)
                labels = ['친화성', '성실성', '외향성', '신경성', '개방성']
        elif len(probabilities) == len(self.emotion_labels):
            labels = self.emotion_labels
        else:
            labels = [f"감정_{i}" for i in range(num_classes)]
        
        result = {label: float(prob) for label, prob in zip(labels, probabilities)}
        return result
    
    def predict_batch(self, texts: List[str]) -> List[Dict[str, float]]:
        """
        여러 텍스트의 감정을 배치로 예측합니다.
        
        Args:
            texts: 분석할 텍스트 리스트
            
        Returns:
            각 텍스트별 감정 확률 리스트
        """
        results = []
        for text in texts:
            result = self.predict_emotion(text)
            results.append(result)
        return results
    
    def get_top_emotion(self, text: str) -> Tuple[str, float]:
        """
        텍스트의 가장 높은 확률의 감정을 반환합니다.
        
        Args:
            text: 분석할 텍스트
            
        Returns:
            (감정명, 확률) 튜플
        """
        emotions = self.predict_emotion(text)
        top_emotion = max(emotions.items(), key=lambda x: x[1])
        return top_emotion
    
    def predict(self, text: str) -> Dict[str, float]:
        """
        predict_emotion의 별칭 메서드 (호환성을 위해)
        
        Args:
            text: 분석할 텍스트
            
        Returns:
            감정별 확률 딕셔너리
        """
        return self.predict_emotion(text)

# 사용 예시
if __name__ == "__main__":
    # 모델 초기화
    detector = BERTEmotionDetector()
    
    # 테스트 텍스트
    test_texts = [
        "오늘 정말 기분이 좋아요! 새로운 프로젝트가 성공적으로 완료되었어요.",
        "너무 슬프고 우울해요. 모든 것이 잘못되어가는 것 같아요.",
        "이건 정말 말도 안 돼! 너무 화가 나요!",
        "내일 발표가 있어서 너무 걱정되고 무서워요.",
        "와! 정말 놀랍네요. 이런 일이 일어날 줄 몰랐어요."
    ]
    
    print("\n🎭 감정 분석 결과:")
    print("=" * 50)
    
    for text in test_texts:
        print(f"\n📝 텍스트: {text}")
        
        # 전체 감정 분석
        emotions = detector.predict_emotion(text)
        print("📊 감정 분포:")
        for emotion, prob in emotions.items():
            print(f"  {emotion}: {prob:.3f}")
        
        # 최고 감정
        top_emotion, top_prob = detector.get_top_emotion(text)
        print(f"🏆 주요 감정: {top_emotion} ({top_prob:.3f})")
        print("-" * 30)