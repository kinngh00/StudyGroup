import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/atoms/Button";
import { Input } from "@/components/atoms/Input";

const schema = z.object({
  name: z.string().optional(),
  finished: z.boolean().default(false)
});

type SearchFormValues = z.infer<typeof schema>;

interface SearchRequestFormProps {
  onSubmit: (values: SearchFormValues) => void;
}

export const SearchRequestForm = ({ onSubmit }: SearchRequestFormProps) => {
  const { register, handleSubmit } = useForm<SearchFormValues>({
    resolver: zodResolver(schema),
    defaultValues: { name: "", finished: false }
  });

  return (
    <form className="panel grid gap-3 p-4 md:grid-cols-[1fr_auto_auto]" onSubmit={handleSubmit(onSubmit)}>
      <Input placeholder="스터디 이름으로 검색" {...register("name")} />
      <label className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 text-sm text-slate-700">
        <input type="checkbox" {...register("finished")} />
        종료된 스터디 포함
      </label>
      <Button type="submit">검색</Button>
    </form>
  );
};
