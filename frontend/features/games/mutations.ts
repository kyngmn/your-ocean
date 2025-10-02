import { createGameSession, finishBARTRound, finishGameSession, submitBARTFirstRoundClick, submitBARTRoundClick, submitGNGClick, submitUGRound } from "@/app/actions/games";
import { handleServerAction } from "@/hooks/useServerActions";
import { BARTFirstRoundClickRequestDTO, BARTRoundFinishRequestDTO, BARTRoundRequestDTO, GNGClickRequestDTO, UGRoundRequestDTO } from "@/types/dto";
import { GameType } from "@/types/enums";
import { useMutation, UseMutationOptions } from "@tanstack/react-query";

// 게임 세션 생성
export const useMutationGameSession = (
  options?: UseMutationOptions<unknown, Error, GameType>
) => {
  return useMutation({
    mutationFn: (gameType: GameType) => 
      handleServerAction(createGameSession(gameType)),
    ...options,
  });
};


// 게임 세션 종료
export const useMutationFinishGameSession = (
  options?: UseMutationOptions<unknown, Error, number>
) => {
  return useMutation({
    mutationFn: (sessionId: number) => handleServerAction(finishGameSession(sessionId)),
    ...options,
  });
};


// GNG 라운드 요청
export const useMutationGNGRound = (
  options?: UseMutationOptions<unknown, Error, { sessionId: number; roundIndex: number; clickData: GNGClickRequestDTO }>
) => {
  return useMutation({
    mutationFn: ({ sessionId, roundIndex, clickData }: { sessionId: number; roundIndex: number; clickData: GNGClickRequestDTO }) => {
      return handleServerAction(submitGNGClick(sessionId, roundIndex, clickData))
    },
    ...options,
  });
};


// UG 게임 라운드 요청
export const useMutationUGRound = (
  options?: UseMutationOptions<unknown, Error, { sessionId: number; roundId: number; roundData: UGRoundRequestDTO }>
) => {
  return useMutation({
    mutationFn: ({ sessionId, roundId, roundData }: { sessionId: number; roundId: number; roundData: UGRoundRequestDTO }) => {
      return handleServerAction(submitUGRound(sessionId, roundId, roundData))
    },
    ...options,
  });
};

// BART 첫 번째 라운드 클릭 요청
export const useMutationBARTFirstRoundClick = (
  options?: UseMutationOptions<unknown, Error, { sessionId: number; roundIndex: number; clickData: BARTFirstRoundClickRequestDTO }>
) => {
  return useMutation({
    mutationFn: ({ sessionId, roundIndex, clickData }: { sessionId: number; roundIndex: number; clickData: BARTFirstRoundClickRequestDTO }) => {
      return handleServerAction(submitBARTFirstRoundClick(sessionId, roundIndex, clickData))
    },
    ...options,
  });
};

// BART 라운드 클릭 요청
export const useMutationBARTRoundClick = (
  options?: UseMutationOptions<unknown, Error, { sessionId: number; roundIndex: number; clickData: BARTRoundRequestDTO }>
) => {
  return useMutation({
    mutationFn: ({ sessionId, roundIndex, clickData }: { sessionId: number; roundIndex: number; clickData: BARTRoundRequestDTO }) => {
      return handleServerAction(submitBARTRoundClick(sessionId, roundIndex, clickData))
    },
    ...options,
  });
};

// BART 라운드 종료 요청
export const useMutationBARTRoundFinish = (
  options?: UseMutationOptions<unknown, Error, { sessionId: number; roundIndex: number; clickData: BARTRoundFinishRequestDTO }>
) => {
  return useMutation({
    mutationFn: ({ sessionId, roundIndex, clickData }: { sessionId: number; roundIndex: number; clickData: BARTRoundFinishRequestDTO }) => {
      return handleServerAction(finishBARTRound(sessionId, roundIndex, clickData))
    },
    ...options,
  });
};
