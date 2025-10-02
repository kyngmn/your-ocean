import SpinnerEllipsis from "./Spinner";

export default function Loading() {
  return (
    <div className="page flex items-center justify-center">
      <SpinnerEllipsis />
      <div>로딩 중</div>
    </div>
  )
}
