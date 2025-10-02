import { removeFriend } from "@/app/actions/friends";
import { handleServerAction } from "@/hooks/useServerActions";
import { dev } from "@/lib/dev";
import { useMutation, UseMutationOptions } from "@tanstack/react-query";

// 친구 삭제
export const useMutationRemoveFriend = (
  friendId: number,
  options?: UseMutationOptions<unknown, Error, void>
) => {
  dev.log("useMutationRemoveFriend", friendId)
  return useMutation({
    mutationFn: () => 
      handleServerAction(removeFriend(friendId)),
    ...options,
  });
};
