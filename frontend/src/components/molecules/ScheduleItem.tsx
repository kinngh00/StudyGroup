import type { StudySchedule } from "@/types/domain";

interface ScheduleItemProps {
  schedule: StudySchedule;
}

export const ScheduleItem = ({ schedule }: ScheduleItemProps) => (
  <div className="rounded-xl border border-slate-200 bg-white p-3 shadow-sm">
    <p className="font-semibold">{schedule.title}</p>
    <p className="text-sm text-slate-600">{new Date(schedule.dateTime).toLocaleString()}</p>
    {schedule.description ? <p className="mt-1 text-sm text-slate-700">{schedule.description}</p> : null}
  </div>
);
