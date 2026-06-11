create table if not exists public.medicamentos (
    id integer generated always as identity primary key,
    nome text not null,
    dose text not null,
    horario text not null check (horario ~ '^([01][0-9]|2[0-3]):[0-5][0-9]$'),
    tomado_hoje boolean not null default false
);

alter table public.medicamentos enable row level security;

drop policy if exists "Permitir leitura anonima de medicamentos" on public.medicamentos;
drop policy if exists "Permitir escrita anonima de medicamentos" on public.medicamentos;

create policy "Permitir leitura anonima de medicamentos"
on public.medicamentos
for select
to anon
using (true);

create policy "Permitir escrita anonima de medicamentos"
on public.medicamentos
for all
to anon
using (true)
with check (true);
