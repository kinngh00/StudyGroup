import { Outlet, useParams } from "react-router-dom";
import { useEffect } from "react";
import { useGetUserRoleQuery } from "@/api/baseApi";
import { useAppDispatch } from "@/app/hooks";
import { setUserRoleInCurrentStudy } from "@/features/study/studySlice";

export const StudyRoleGate = () => {
  const { studyId } = useParams();
  const numericStudyId = Number(studyId);
  const dispatch = useAppDispatch();
  const { data } = useGetUserRoleQuery(numericStudyId, { skip: Number.isNaN(numericStudyId) });

  useEffect(() => {
    if (data?.role) {
      dispatch(setUserRoleInCurrentStudy(data.role));
    } else {
      dispatch(setUserRoleInCurrentStudy("None"));
    }
  }, [data, dispatch]);

  return <Outlet />;
};
