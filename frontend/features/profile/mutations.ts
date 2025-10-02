import { createInviteLink } from "@/app/actions/friends";
import { deleteUser } from "@/app/actions/users";
import { handleServerAction } from "@/hooks/useServerActions";
import { useMutation, UseMutationOptions } from "@tanstack/react-query";


// 친구 초대 링크 생성
export const useMutationCreateInviteLink = (
  options?: UseMutationOptions<any, Error, void>
) => {
  return useMutation({
    mutationFn: () => 
      handleServerAction(createInviteLink()),
    ...options,
  });
};


// 회원탈퇴
export const useMutationDeleteUser = (
  options?: UseMutationOptions<any, Error, void>
) => {
  return useMutation({
    mutationFn: () => 
      handleServerAction(deleteUser()),
    ...options,
  });
};