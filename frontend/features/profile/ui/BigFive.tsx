import Typography from '../../../components/ui/Typography';
import ProgressBar from './BigFiveProgressBar';

interface BigFiveProps {
  values: {
    O: number; // Openness
    C: number; // Conscientiousness  
    E: number; // Extraversion
    A: number; // Agreeableness
    N: number; // Neuroticism
  };
}

export default function BigFive({values }: BigFiveProps) {
  const indicators = [
    { key: 'O', title: '개방성', value: values.O },
    { key: 'C', title: '성실성', value: values.C },
    { key: 'E', title: '외향성', value: values.E },
    { key: 'A', title: '친화성', value: values.A },
    { key: 'N', title: '신경성', value: values.N },
  ];
  
  return (
    <div className="w-full">
      
      {/* 5가지 지표 */}
      <div className="space-y-6">
        {indicators.map((indicator) => (
            <div key={indicator.key} className="w-full">
            {/* 키, 타이틀, Progress Bar 한 줄 표시 */}
            <div className="flex items-center gap-3 mb-2">
              <Typography type="p">{indicator.key} {indicator.title}</Typography>
              <div className="flex-1">
                <ProgressBar 
                  value={indicator.value} 
                  indicator={indicator.key as 'O' | 'C' | 'E' | 'A' | 'N'} 
                />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}