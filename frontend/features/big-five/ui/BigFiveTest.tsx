"use client"

import Typography from "@/components/ui/Typography";  

interface BigFiveTestProps {
    question: string;
    selectedValue?: number; // 1-5 선택된 값
    onValueChange?: (value: number) => void;
}

export default function BigFiveTest({ question, selectedValue, onValueChange }: BigFiveTestProps) {
    // 동그라미 크기 계산 (양 끝이 가장 크고 가운데로 갈수록 작아짐) - 반응형
    const getCircleSize = (index: number) => {
        const sizes = ['w-6 h-6 sm:w-8 sm:h-8 md:w-10 md:h-10 lg:w-12 lg:h-12', 'w-5 h-5 sm:w-6 sm:h-6 md:w-8 md:h-8 lg:w-10 lg:h-10', 'w-4 h-4 sm:w-5 sm:h-5 md:w-6 md:h-6 lg:w-8 lg:h-8', 'w-5 h-5 sm:w-6 sm:h-6 md:w-8 md:h-8 lg:w-10 lg:h-10', 'w-6 h-6 sm:w-8 sm:h-8 md:w-10 md:h-10 lg:w-12 lg:h-12'];
        return sizes[index];
    };

    return (
        <div className=" w-full">
            {/* 질문 */}
            <div className="text-center mb-8">
                <Typography type="large">{question}</Typography>
            </div>
            
            {/* Radio Group */}
            <div className="flex justify-center items-center mb-8">
                <div className="flex items-center">
                    {Array.from({ length: 5 }, (_, index) => {
                        const value = index + 1;
                        const isSelected = selectedValue === value;
                        const circleSize = getCircleSize(index);
                        
                        return (
                            <div key={index} className="flex items-center">
                                {/* Radio Button */}
                                <label className="cursor-pointer">
                                    <input
                                        type="radio"
                                        name="bigFiveTest"
                                        value={value}
                                        checked={isSelected}
                                        onChange={() => onValueChange?.(value)}
                                        className="sr-only"
                                    />
                                    <div 
                                        className={`${circleSize} rounded-full border-2 flex items-center justify-center transition-all duration-200 ${
                                            isSelected 
                                                ? 'bg-black border-black' 
                                                : 'bg-white border-gray-300 hover:border-gray-400'
                                        }`}
                                    />
                                </label>
                                
                                {/* 연결선 (마지막 원이 아닌 경우) - 반응형 */}
                                {index < 4 && (
                                    <div className="w-6 sm:w-8 md:w-10 lg:w-12 xl:w-14 h-0.5 bg-gray-300" />
                                )}
                            </div>
                        );
                    })}
                </div>
            </div>
            
            {/* 단계 텍스트 - 양 끝단 원 바로 밑에 배치 */}
            <div className="flex justify-center items-start">
                <div className="flex items-start">
                    {Array.from({ length: 5 }, (_, index) => {
                        const circleSize = getCircleSize(index);
                        const isFirstOrLast = index === 0 || index === 4;
                        
                        return (
                            <div key={index} className="flex items-start">
                                {/* 텍스트 컨테이너 - 높이 없이 */}
                                <div className={`${circleSize} flex justify-center`}>
                                    {isFirstOrLast && (
                                        <Typography type="small" className="whitespace-nowrap">
                                            {index === 0 ? '전혀 아니다' : '매우 그렇다'}
                                        </Typography>
                                    )}
                                </div>
                                
                                {/* 연결선 (마지막 원이 아닌 경우) - 반응형 */}
                                {index < 4 && (
                                    <div className="w-6 sm:w-8 md:w-10 lg:w-12 xl:w-14 h-0.5 bg-transparent" />
                                )}
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}