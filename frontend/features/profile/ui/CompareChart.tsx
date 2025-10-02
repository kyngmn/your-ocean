"use client";

import { useState } from 'react';
import { Radar } from 'react-chartjs-2';
import { Chart as ChartJS, RadialLinearScale, PointElement, LineElement, Filler, Legend } from 'chart.js';
import Typography from '@/components/ui/Typography';

// Chart.js 구성 요소 등록
ChartJS.register(RadialLinearScale, PointElement, LineElement, Filler, Legend);

interface CompareChartProps {
  report1?: any;
  report2?: any;
}

export default function CompareChart({ report1, report2 }: CompareChartProps) {
  const [visibleDatasets, setVisibleDatasets] = useState<boolean[]>([true, true]);

  // 데이터 준비
  const labels = ['개방성', '성실성', '외향성', '친화성', '신경성'];
  const data1 = report1 ? [
    report1.O,
    report1.C,
    report1.E,
    report1.A,
    report1.N
  ] : [0, 0, 0, 0, 0];

  const data2 = report2 ? [
    report2.O,
    report2.C,
    report2.E,
    report2.A,
    report2.N
  ] : [0, 0, 0, 0, 0];

  const radarData = {
    labels,
    datasets: [
      {
        label: '자기보고식',
        data: data1,
        backgroundColor: visibleDatasets[0] ? 'rgba(59, 130, 246, 0.2)' : 'transparent',
        borderColor: visibleDatasets[0] ? '#3B82F6' : 'transparent',
        borderWidth: 2,
        pointBackgroundColor: visibleDatasets[0] ? '#3B82F6' : 'transparent',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: '#3B82F6'
      },
      {
        label: '행동 분석',
        data: data2,
        backgroundColor: visibleDatasets[1] ? 'rgba(255, 236, 179, 0.5)' : 'transparent',
        borderColor: visibleDatasets[1] ? '#F59E0B' : 'transparent',
        borderWidth: 2,
        pointBackgroundColor: visibleDatasets[1] ? '#F59E0B' : 'transparent',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: '#F59E0B'
      }
    ]
  };

  // 차이 계산 (가장 일치/가장 차이)
  const diffs = labels.map((label, i) => ({ label, diff: Math.abs(data1[i] - data2[i]) }));
  const mostSimilar = diffs.reduce((min, cur) => (cur.diff < min.diff ? cur : min), diffs[0]);
  const mostDifferent = diffs.reduce((max, cur) => (cur.diff > max.diff ? cur : max), diffs[0]);

  const radarOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      title: {
        display: true,
        text: 'BIG5 비교 차트',
        font: {
          size: 16,
          weight: 'bold' as const
        }
      },
      legend: {
        display: false // 기본 범례 숨김
      }
    },
    scales: {
      r: {
        min: 0,
        max: 100,
        ticks: {
          stepSize: 20,
          display: false
        },
        grid: {
          color: 'rgba(0, 0, 0, 0.1)'
        },
        angleLines: {
          color: 'rgba(0, 0, 0, 0.1)'
        },
        pointLabels: {
          font: {
            size: 12
          }
        }
      }
    }
  };

  return (
    <div className="w-full space-y-4">
      {/* 차트 */}
      <div className="w-full h-96">
        <Radar data={radarData} options={radarOptions} />
      </div>
      
      {/* 커스텀 범례 */}
      <div className="flex justify-center gap-4">
        {radarData.datasets.map((dataset, index) => (
          <div
            key={index}
            className={`flex items-center gap-2 px-3 py-1 rounded-full text-sm font-medium cursor-pointer transition-all ${
              visibleDatasets[index] ? 'opacity-100' : 'opacity-50'
            }`}
            style={{
              backgroundColor: visibleDatasets[index] 
                ? dataset.borderColor + '20' 
                : '#f3f4f6',
              color: 'black',
            }}
            onClick={() => {
              const newVisible = [...visibleDatasets];
              newVisible[index] = !newVisible[index];
              setVisibleDatasets(newVisible);
            }}
          >
            {dataset.label}
          </div>
        ))}
      </div>

      {/* 일치/차이 지표 요약 */}
      <div className="text-center text-sm space-y-1">
        <Typography type="p">
          가장 일치한 지표: <span className="font-semibold">{mostSimilar.label}</span> (차이 {(mostSimilar.diff).toFixed(0)}%)
        </Typography>
        <Typography type="p">
          가장 차이나는 지표: <span className="font-semibold">{mostDifferent.label}</span> (차이 {(mostDifferent.diff).toFixed(0)}%)
        </Typography>
      </div>
    </div>
  );
}