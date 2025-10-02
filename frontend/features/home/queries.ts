import { getGameCount, getTodayMessage } from "@/app/actions/diaries";
import { handleServerAction } from "@/hooks/useServerActions";
import { useQuery } from "@tanstack/react-query";

// 게임 카운트 조회
export const useGameCount = () => {
  return useQuery({
    queryKey: ["gameCount"],
    queryFn: () => handleServerAction(getGameCount()),
  });
};

// 오늘의 메시지 조회
export const useTodayMessage = () => {
  return useQuery({
    queryKey: ["todayMessage"],
    queryFn: () => handleServerAction(getTodayMessage()),
  });
};
