import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogClose } from "@radix-ui/react-dialog";
import { useState, useRef, useEffect } from "react";
import Image from "next/image";
import { UserDTO } from "@/types/dto";
import { Upload } from "lucide-react";
import { updateUser } from "@/app/actions/users";
import { useAuthStore } from "@/stores/auth-store";
import { toast } from "sonner";
import { dev } from "@/lib/dev";

// 유저 업데이트 타입
interface ProfileUpdateDialogProps extends Partial<UserDTO> {
  children: React.ReactNode;
}

export default function ProfileUpdateDialog({ children, profileImageUrl, nickname="닉네임" }: ProfileUpdateDialogProps) {
  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [newNickname, setNewNickname] = useState(nickname);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { updateUser: updateUserInStore } = useAuthStore();
  
  // 기본 프로필 이미지 경로
  const defaultProfileImage = "/image/default_profile.png";

  // props가 변경될 때 내부 상태 업데이트
  useEffect(() => {
    setNewNickname(nickname);
  }, [nickname]);

  // 이미지 변경 핸들러
  const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      // 파일 크기 검증 (10MB 제한)
      const maxSize = 10 * 1024 * 1024; // 10MB
      if (file.size > maxSize) {
        toast.error("파일 크기는 10MB 이하여야 합니다.");
        return;
      }
      
      setSelectedFile(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setPreviewImage(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  // 사용자 정보 업데이트
  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsLoading(true);
    try {

      // 업데이트할 데이터가 있는 경우에만 API 호출
      if (newNickname !== nickname || selectedFile) {

        const res= await updateUser(newNickname, selectedFile as File);
        
        if (res.isSuccess && res.data) {
          // Zustand store 업데이트
          updateUserInStore(res.data);
          toast.success("프로필이 성공적으로 업데이트되었습니다.");
          
          // 상태 초기화
          setPreviewImage(null);
          setSelectedFile(null);
          setNewNickname(res.data.nickname || nickname);
          setIsOpen(false);
        } else {
          if (res.error) {
            toast.error(res.error);
          } else {
            toast.error("프로필 업데이트에 실패했습니다.");
          }
        }
      } else {
        toast.info("변경된 내용이 없습니다.");
      }
    } catch (error) {
      dev.error("Profile update error:", error);
      toast.error("프로필 업데이트 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      {children}
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>프로필 수정</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="grid gap-4">
            <div className="grid gap-3">
              <div className="flex flex-col items-center gap-3">
                {/* 미리보기 영역 */}
                <div className="w-20 h-20 rounded-full overflow-hidden border-2 border-gray-200">
                  <Image
                    src={previewImage || profileImageUrl || defaultProfileImage}
                    alt="프로필 미리보기"
                    width={80}
                    height={80}
                    className="w-full h-full object-cover"
                  />
                </div>
                
                {/* 파일 선택 버튼 */}
                <Input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleImageChange}
                  className="hidden"
                />
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => fileInputRef.current?.click()}
                  disabled={isLoading}
                >
                  <Upload className="w-4 h-4 mr-2" />
                  이미지 업로드
                </Button>
              </div>
            </div>

            <div className="grid gap-3">
              <Label htmlFor="nickname">닉네임</Label>
              <Input 
                id="nickname" 
                name="nickname" 
                value={newNickname}
                onChange={(e) => setNewNickname(e.target.value)}
                placeholder="닉네임을 입력하세요"
                disabled={isLoading}
              />
            </div>
          </div>
          <DialogFooter className="mt-4">
            <DialogClose asChild>
              <Button 
                variant="outline" 
                type="button"
                disabled={isLoading}
                onClick={() => {
                  setIsOpen(false);
                  setPreviewImage(null);
                  setSelectedFile(null);
                  setNewNickname(nickname);
                }}
              >
                취소
              </Button>
            </DialogClose>
            <Button 
              type="submit" 
              disabled={isLoading}
            >
              {isLoading ? "저장 중" : "저장"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}