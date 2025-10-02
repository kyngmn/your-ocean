import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import Typography from "@/components/ui/Typography";
import { useMutationRemoveFriend } from "../mutations";
import { toast } from "sonner";
import { dev } from "@/lib/dev";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

export default function DeleteDialog({ children, friendId }: { children: React.ReactNode, friendId: number }) {
  const [open, setOpen] = useState(false);
  const queryClient = useQueryClient();

  // 친구 삭제
  const deleteFriendMutation = useMutationRemoveFriend(friendId, {
    onSuccess: () => {
      toast.success("친구가 삭제되었습니다.")
      setOpen(false)
      // 추가로 쿼리 무효화 및 refetch
      queryClient.invalidateQueries({ queryKey: ['friends'] });
      queryClient.refetchQueries({ queryKey: ['friends'] });
      dev.log("🔥 DeleteDialog에서 쿼리 무효화 완료");
    },
    onError: (error) => {
      dev.error("친구 삭제 실패:", error)
      toast.error("친구 삭제에 실패했습니다.")
    }
  })

  // 친구 삭제
  const handleDelete = () => {
    deleteFriendMutation.mutate()
  }


  return (
    <Dialog open={open} onOpenChange={setOpen}>
      {children}
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>친구 삭제</DialogTitle>
        </DialogHeader>
          <Typography type="p">정말 삭제하시겠어요?</Typography>
          <DialogFooter>
            <Button variant="secondary" onClick={() => setOpen(false)}>취소</Button>
            <Button variant="destructive" onClick={handleDelete}>확인</Button>
          </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}