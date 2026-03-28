interface AvatarProps {
  name: string;
  src?: string;
}

export const Avatar = ({ name, src }: AvatarProps) => (
  <div className="flex items-center gap-2">
    <div className="h-9 w-9 overflow-hidden rounded-full border border-slate-200 bg-white shadow-sm">
      {src ? (
        <img src={src} alt={name} className="h-full w-full object-cover" />
      ) : (
        <div className="flex h-full w-full items-center justify-center bg-brand-100 text-brand-700">{name[0]}</div>
      )}
    </div>
    <span className="text-sm font-medium text-slate-700">{name}</span>
  </div>
);
