import { create } from 'zustand';

export interface BigFiveScores {
  O: number;
  C: number;
  E: number;
  A: number;
  N: number;
}

// 스토어 타입
interface ReportStore {
  selfReportBigFive: BigFiveScores | null;
  // finalReportBigFive: BigFiveScores | null;
  setSelfReportBigFive: (bigFiveScores: Record<string, number>) => BigFiveScores;
  // setFinalReportBigFive: (bigFiveScores: BigFiveScores) => BigFiveScores;
  calculateBigFiveScores: (bigFiveScores: Record<string, number>) => BigFiveScores;
}

// 스토어 생성
export const useReportStore = create<ReportStore>((set, get) => ({
  selfReportBigFive: null,
  // finalReportBigFive: null,
  
  // [Self] 빅파이브 점수 계산
  calculateBigFiveScores: (bigFiveScores: Record<string, number>) => {
    const scores = { O: 0, C: 0, E: 0, A: 0, N: 0 };
    
    Object.entries(bigFiveScores).forEach(([key, value]) => {
      const prefix = key.charAt(0) as keyof typeof scores;
      if (scores.hasOwnProperty(prefix)) {
        scores[prefix] += value;
      }
    });
    
    return scores;
  },
  
  // [Self] 빅파이브 점수 계산 후 스토어에 저장
  setSelfReportBigFive: (bigFiveScores: Record<string, number>) => {
    const calculatedScores = get().calculateBigFiveScores(bigFiveScores);
    set({ selfReportBigFive: calculatedScores });
    return calculatedScores;
  },

  // // [Final] Report 스토어에 저장
  // setFinalReportBigFive: (bigFiveScores: BigFiveScores) => {
  //   set({ finalReportBigFive: bigFiveScores });
  //   return bigFiveScores;
  // },

}));
