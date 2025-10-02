#!/bin/bash

# BERT 감정 분석 환경 설정 스크립트

echo "🚀 BERT 감정 분석 환경을 설정합니다..."

# conda 환경 생성
echo "📦 conda 환경 생성 중..."
conda env create -f environment.yml

echo "✅ 환경이 성공적으로 생성되었습니다!"
echo ""
echo "🎯 환경을 활성화하려면 다음 명령어를 실행하세요:"
echo "conda activate bert-emotion"
echo ""
echo "🌟 Streamlit 앱을 실행하려면:"
echo "streamlit run streamlit_demo.py"