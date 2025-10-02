import { useQuery } from "@tanstack/react-query";
import { getFinalReport, getSelfReport } from "@/app/actions/reports";
import { handleServerAction } from "@/hooks/useServerActions";
import { getUserPersonas } from "@/app/actions/users";

// SELF 리포트 조회
export const useSelfReport = () => {
  return useQuery({
    queryKey: ["selfReport"],
    queryFn: () => handleServerAction(getSelfReport()),
  });
};


// FINAL 리포트 조회
export const useFinalReport = () => {
  return useQuery({
    queryKey: ["finalReport"],
    queryFn: () => handleServerAction(getFinalReport()),
  });
};



//나의 페르소나 조회
export const useUserPersonas = () => {
  return useQuery({
    queryKey: ["userPersonas"],
    queryFn: () => handleServerAction(getUserPersonas()),
  });
};