import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { DialogClose } from "@radix-ui/react-dialog"
import Typography from "@/components/ui/Typography"
import { useRouter } from "next/navigation"
import { useMutationDeleteUser } from "@/features/profile/mutations"
import { useAuthStore } from "@/stores/auth-store"
import { toast } from "sonner"
import { dev } from "@/lib/dev"
import SpinnerEllipsis from "@/components/common/Spinner"

export default function SignOutDialog({ children }: { children: React.ReactNode }) {
  const router = useRouter()
  const logout = useAuthStore((state) => state.logout)

  const deleteUserMutation = useMutationDeleteUser({
    onSuccess: async () => {
      // auth-store 상태만 정리 (쿠키는 이미 deleteUser에서 삭제됨)
      const logoutResult = await logout()
      dev.log("🔥 로그아웃 결과:", logoutResult)

      router.push("/login")
    },
    onError: (error) => {
      dev.error("회원탈퇴 실패:", error)
      toast.error("회원탈퇴에 실패했습니다. 다시 시도해주세요.")
    }
  })

  const handleDeleteUser = () => {
    deleteUserMutation.mutate()
  }

  return (
    <Dialog>
      {children}
      <DialogContent className="sm:max-w-[425px]">
        {deleteUserMutation.isPending && (
          <>
            <DialogHeader>
              <DialogTitle>회원탈퇴</DialogTitle>
            </DialogHeader>
            <SpinnerEllipsis />
            <Typography type="p" className="text-center">처리 중</Typography>
          </>
        )}
        {!deleteUserMutation.isPending && (
          <>
            <DialogHeader>
              <DialogTitle>회원탈퇴</DialogTitle>
            </DialogHeader>
            <Typography type="p">회원 탈퇴 하시겠습니까?</Typography>
            <DialogFooter>
              <DialogClose asChild>
                <Button variant="secondary">취소</Button>
              </DialogClose>
              <Button variant="destructive" onClick={handleDeleteUser}>
                확인
              </Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  )
}
