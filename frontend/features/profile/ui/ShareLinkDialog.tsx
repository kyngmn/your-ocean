import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { useEffect, useRef, useState } from "react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { dev } from "@/lib/dev"
import { toast } from "sonner"
import { useMutationCreateInviteLink } from "../mutations"

export default function ShareLinkDialog({ children }: { children: React.ReactNode }) {
  const [copied, setCopied] = useState(false)
  const [inviteUrl, setInviteUrl] = useState<string>("")
  const [open, setOpen] = useState(false)
  const inputRef = useRef<HTMLInputElement>(null)

  const createInviteMutation = useMutationCreateInviteLink({
    onSuccess: (data) => {
      dev.log("🔥 초대 링크 생성 성공:", data)
      // 프론트엔드 URL로 변환
      const code = data.invitationToken
      const nickname = data.inviterNickname
      const profileImage = data.inviterProfileImageUrl || "/image/default_profile.png"
      const frontendUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}/handler/friend/?code=${code}&nickname=${nickname}&profileImage=${profileImage}`
      setInviteUrl(frontendUrl)
    },
    onError: (error) => {
      dev.error("🔥 초대 링크 생성 실패:", error)
      toast.error("초대 링크 생성에 실패했습니다. 다시 시도해주세요.")
    }
  })

  // 다이얼로그가 열릴 때만 초대 링크 생성
  useEffect(() => {
    if (open && !inviteUrl && !createInviteMutation.isPending) {
      createInviteMutation.mutate()
    }
  }, [open])

  // 다이얼로그가 닫힐 때 상태 초기화
  useEffect(() => {
    if (!open) {
      setInviteUrl("")
      setCopied(false)
    }
  }, [open])

  // 링크 복사
  const handleCopy = async () => {
    if (inviteUrl) {
      try {
        await navigator.clipboard.writeText(inviteUrl)
        setCopied(true)
        setTimeout(() => setCopied(false), 2000) // 2초 후 복사 상태 초기화
      } catch (err) {
        dev.error("링크 복사 실패:", err)
      }
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      {children}
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>친구 추가 링크</DialogTitle>
          <DialogDescription>해당 링크를 통해 친구를 맺을 수 있어요!</DialogDescription>
        </DialogHeader>

        {createInviteMutation.isPending ? (
          <div className="flex justify-center py-4">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        ) : (
          <div className="flex items-center gap-2">
            <div className="grid flex-1 gap-2">
              <Label htmlFor="link" className="sr-only">
                Link
              </Label>
              <Input ref={inputRef} id="link" value={inviteUrl} readOnly />
            </div>

            <Button
              variant="ghost"
              onClick={handleCopy}
              className="hover:bg-transparent min-w-20"
              style={copied ? { color: "var(--accent-blue)" } : { color: "" }}
              disabled={!inviteUrl}
            >
              {copied ? "복사됨!" : "복사"}
            </Button>
          </div>
        )}
      </DialogContent>
    </Dialog>
  )
}
