import { getFriends } from "@/app/actions/friends";
import { handleServerAction } from "@/hooks/useServerActions";
import { useQuery } from "@tanstack/react-query";


// 친구 목록 조회
export const useGetFriends = (options = {}) => {
  return useQuery({
    queryKey: ["friends"],
    queryFn: () => handleServerAction(getFriends()),
    ...options,
  });
};

