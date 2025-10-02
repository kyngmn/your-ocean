"use client";

import { useEffect, useState } from 'react';
import { PersonaCode } from '@/types/enums';
import { Badge } from '@/components/ui/badge';
import Typography from '@/components/ui/Typography';
import { dev } from '@/lib/dev';

interface SmallCodeChartProps {
  smallCodes: any;
  selectedOcean: PersonaCode;
}

export default function SmallCodeChart({ smallCodes, selectedOcean }: SmallCodeChartProps) {
  const [selectedLabel, setSelectedLabel] = useState<string | null>(null);

  // 디버깅을 위한 로그
  dev.log("🔥 SmallCodeChart 받은 데이터:", { smallCodes, selectedOcean });

  // OCEAN이 바뀔 때 selectedLabel 초기화
  useEffect(() => {
    setSelectedLabel(null);
  }, [selectedOcean]);

  if (!smallCodes) {
    throw new Error("🔥 SmallCodeChart: smallCodes가 없음");
  }

  // Small code 데이터를 0-5 스케일로 변환 (원본은 0-20점)
  const normalizeScore = (score: number) => Math.round((score / 20) * 5);

  // OCEAN 타입별 설정 (차트용)
  const oceanConfig = {
    O: {
      name: '개방성',
      labels: ['상상력', '예술적 관심', '감정성', '모험심', '지적 호기심', '자유주의'],
      codes: ['O1', 'O2', 'O3', 'O4', 'O5', 'O6'],
      color: 'rgba(255, 236, 179, 0.5)', 
      borderColor: '#FFECB3'
    },
    C: {
      name: '성실성',
      labels: ['자신감', '계획성', '책임감', '성취 추구', '자제력', '신중함'],
      codes: ['C1', 'C2', 'C3', 'C4', 'C5', 'C6'],
      color: 'rgba(200, 230, 201, 0.2)', 
      borderColor: '#C8E6C9'
    },
    E: {
      name: '외향성',
      labels: ['친밀감', '사교성', '자기주장', '활동성', '흥미 추구', '쾌활함'],
      codes: ['E1', 'E2', 'E3', 'E4', 'E5', 'E6'],
      color: 'rgba(187, 222, 251, 0.2)', 
      borderColor: '#BBDEFB'
    },
    A: {
      name: '친화성',
      labels: ['신뢰', '도덕성', '이타심', '협력', '겸손', '공감'],
      codes: ['A1', 'A2', 'A3', 'A4', 'A5', 'A6'],
      color: 'rgba(225, 190, 231, 0.2)',
      borderColor: '#E1BEE7'
    },
    N: {
      name: '신경성',
      labels: ['불안', '분노', '우울', '자의식', '충동성', '심약함'],
      codes: ['N1', 'N2', 'N3', 'N4', 'N5', 'N6'],
      color: 'rgba(255, 171, 171, 0.2)', 
      borderColor: '#FFABAB'
    }
  };

  const currentConfig = oceanConfig[selectedOcean];
  const size = 400;
  const center = size / 2;
  const radius = size / 2 - 60;
  const max = 5;

  // 데이터 가져오기 (bigFiveScores 구조에 맞게)
  const data = currentConfig.codes.map(code => {
    const score = smallCodes[code as keyof typeof smallCodes] || 0;
    const normalized = normalizeScore(score);
    return normalized;
  });
  

  // 각 라벨 위치 계산
  const points = data.map((value, i) => {
    const angle = (2 * Math.PI * i) / currentConfig.labels.length - Math.PI / 2;
    const r = (value / max) * radius;
    return [center + Math.cos(angle) * r, center + Math.sin(angle) * r];
  });

  const polygonPoints = points.map(p => p.join(",")).join(" ");
  

  // 각 라벨의 위치 계산 (차트 외부)
  const labelPositions = currentConfig.labels.map((_, i) => {
    const angle = (2 * Math.PI * i) / currentConfig.labels.length - Math.PI / 2;
    const x = center + Math.cos(angle) * (radius + 50);
    const y = center + Math.sin(angle) * (radius + 30);
    return { x, y, angle };
  });

  return (
    <div className="w-full space-y-4">
      {/* SVG 차트와 라벨 Badge */}
      <div className="flex justify-center relative">
        <svg width={size} height={size}>
          {/* 격자 원 */}
          {[...Array(max)].map((_, i) => (
            <circle
              key={i}
              cx={center}
              cy={center}
              r={(radius / max) * (i + 1)}
              fill="none"
              stroke="rgba(0,0,0,0.1)"
            />
          ))}

          {/* 축선 */}
          {currentConfig.labels.map((_, i) => {
            const angle = (2 * Math.PI * i) / currentConfig.labels.length - Math.PI / 2;
            const x = center + Math.cos(angle) * radius;
            const y = center + Math.sin(angle) * radius;
            return <line key={i} x1={center} y1={center} x2={x} y2={y} stroke="rgba(0,0,0,0.1)" />;
          })}

          {/* 데이터 다각형 */}
          <polygon
            points={polygonPoints}
            fill={currentConfig.color}
            stroke={currentConfig.borderColor}
            strokeWidth={2}
          />

          {/* 포인트 */}
          {points.map((p, i) => (
            <circle 
              key={i} 
              cx={p[0]} 
              cy={p[1]} 
              r={4} 
              fill={currentConfig.borderColor} 
              stroke="#fff" 
              strokeWidth={2}
              className="hover:r-6 transition-all"
            />
          ))}
        </svg>

        {/* 각 지표의 Badge 라벨 */}
        {currentConfig.labels.map((label, i) => {
          const pos = labelPositions[i];
          
          return (
            <div
              key={i}
              className="absolute"
              style={{
                left: pos.x,
                top: pos.y,
                transform: 'translate(-50%, -50%)'
              }}
            >
              <Badge
                variant="secondary"
                className={`text-xs cursor-pointer transition-colors whitespace-nowrap`}
                style={{ 
                  backgroundColor: selectedLabel === label 
                    ? currentConfig.borderColor 
                    : '#f3f4f6',
                  border: 'none'
                }}
                onClick={() => {
                  setSelectedLabel(selectedLabel === label ? null : label);
                }}
              >
                {label}
              </Badge>
            </div>
          );
        })}
      </div>

      {/* 선택된 라벨의 설명 */}
      {selectedLabel && (<>
        <div>
          <Typography type="h3">{selectedLabel}</Typography>
        </div>
        <div className="mt-4 p-4 bg-gray-50 rounded-lg">
            {(() => {
              const selectedIndex = currentConfig.labels.indexOf(selectedLabel);
              const selectedCode = currentConfig.codes[selectedIndex];
              const score = smallCodes[selectedCode as keyof typeof smallCodes] || 0;
              
              // 각 지표별 description 매핑
              const descriptions = {
                // 개방성 (O)
                '상상력': '상상력이 풍부하고 창의적인 아이디어를 자주 떠올리는 능력이에요.',
                '예술적 관심': '얼마나 예술과 미술에 대한 깊은 관심과 감성을 가지고 있는지를 나타내요.',
                '감정성': '감정을 풍부하게 표현하고 타인의 감정을 잘 이해하는지를 나타내요.',
                '모험심': '새로운 모험과 도전을 적극적으로 추구하는지를 나타내요.',
                '지적 호기심': '지적 호기심이 강하고 새로운 지식을 탐구하는 능력이에요.',
                '자유주의': '자유로운 사고와 독립적인 가치관을 가지고 있는지를 나타내요.',
                
                // 성실성 (C)
                '자신감': '자신의 능력에 대한 확신이 있고 자신감이 높은지를 보여줘요.',
                '계획성': '체계적인 계획을 세우고 실행하는 능력이 뛰어난지를 보여줘요.',
                '책임감': '책임감이 강하고 맡은 일을 끝까지 해내는 능력이에요.',
                '성취 추구': '성취를 추구하고 목표 달성을 위해 노력하는지를 보여줘요.',
                '자제력': '자제력이 있어 충동적인 행동을 잘 통제하는지 보여줘요.',
                '신중함': '신중하게 생각하고 결정하는 경향인지를 나타내요.',
                
                // 외향성 (E)
                '친밀감': '친밀한 관계를 형성하고 유지하는 능력을 나타내요',
                '사교성': '사교적이고 사람들과 어울리기를 좋아하는지를 보여줘요.',
                '자기주장': '자신의 의견을 당당하게 표현할 수 있는지를 보여줘요.',
                '활동성': '활동적이고 에너지가 넘치는 정도를 나타내요.',
                '흥미 추구': '다양한 흥미와 관심사를 추구하는 정도에요.',
                '쾌활함': '쾌활하고 긍정적인 에너지를 가지고 있는지를 보여줘요.',
                
                // 친화성 (A)
                '신뢰': '타인을 신뢰하고 긍정적으로 바라보는지를 보여줘요.',
                '도덕성': '도덕적 가치관이 확고하고 윤리적인지를 나타내요',
                '이타심': '이타심이 강하고 타인을 돕는 것을 좋아하는지 보여줘요.',
                '협력': '협력적이고 팀워크를 중시하는 정도에요.',
                '겸손': '겸손한 자세로 다른 사람의 의견을 존중하는지를 나타내요.',
                '공감': '타인의 감정과 상황에 공감하는 능력을 나타내요.',
                
                // 신경성 (N)
                '불안': '불안감을 잘 느끼지 않고 안정적인지를 나타내요',
                '분노': '분노를 잘 통제하고 감정 조절이 잘 되는 정도를 나타내요.',
                '우울': '우울감에 잘 빠지지 않고 긍정적인지를 나타내요.',
                '자의식': '자의식이 적고 자연스러운 모습을 보여주는 정도를 나타내요.',
                '충동성': '충동적이지 않고 신중한 판단을 하는지 보여줘요.',
                '심약함': '심약하지 않고 용감한 면모를 보여주는지를 나타내요.'
              };
              
              const description = descriptions[selectedLabel as keyof typeof descriptions] || '';
              return (
                <>
                  <Typography type="p">{selectedLabel} ({score}/20점) </Typography>
                  <Typography type="p">{description}</Typography>
                </>
              );
            })()}
        </div>
      </>)}
    </div>
  );
}
