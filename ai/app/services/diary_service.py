from typing import List


def extract_recommendations(conclusion: str) -> List[str]:
    """결론에서 추천사항 추출"""
    recommendations = []
    sentences = conclusion.split('.')

    for sentence in sentences:
        sentence = sentence.strip()
        if any(keyword in sentence for keyword in ['추천', '제안', '시도', '해보세요', '권장']):
            recommendations.append(sentence)

    return recommendations[:3]
