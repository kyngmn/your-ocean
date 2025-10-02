"use client"

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogTitle,
  AlertDialogTrigger
} from "@/components/ui/alert-dialog"
import { Button } from "@/components/ui/button"
import { type DiaryDTO } from "@/types/dto"
import { Trash2 } from "lucide-react"
import { usePathname, useRouter } from "next/navigation"
import { useState } from "react"
import { toast } from "sonner"
import { useDeleteDiary } from "../queries"

interface Props extends Pick<DiaryDTO, "id"> {
  trigger: React.ReactNode
  onDeleted?: () => void
}

export default function DiaryDeleteDialog({ id, trigger, onDeleted }: Props) {
  const [open, setOpen] = useState(false)
  const router = useRouter()
  const pathname = usePathname()

  const deleteDiaryMutation = useDeleteDiary({
    onSuccess: () => {
      toast.success("일기가 삭제되었습니다.")
      setOpen(false)

      if (onDeleted) {
        onDeleted()
      } else {
        if (pathname === "/diaries") {
          router.refresh()
        } else {
          router.push("/diaries")
        }
      }
    },
    onError: (error) => {
      toast.error("일기 삭제에 실패했습니다.")
      console.error("일기 삭제 실패:", error)
    }
  })

  const handleDelete = () => {
    deleteDiaryMutation.mutate(id)
  }

  return (
    <AlertDialog open={open}>
      <AlertDialogTrigger asChild onClick={() => setOpen(true)}>
        {trigger || (
          <Button variant="destructive" size="sm">
            <Trash2 className="w-4 h-4 mr-1" />
            삭제
          </Button>
        )}
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogTitle>일기 삭제</AlertDialogTitle>
        <AlertDialogDescription>정말 삭제하시겠습니까?</AlertDialogDescription>
        <AlertDialogFooter className="ml-0 sm:ml-auto mr-0">
          <AlertDialogCancel onClick={() => setOpen(false)}>취소</AlertDialogCancel>
          <AlertDialogAction
            onClick={handleDelete}
            disabled={deleteDiaryMutation.isPending}
            className="bg-error hover:bg-error-dark"
          >
            삭제
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
