import { z } from 'zod';
import rootSchema from './rootSchema';

type MaterT = z.infer<typeof rootSchema.Master>;

export namespace RootType {

  export type Master = MaterT;
}