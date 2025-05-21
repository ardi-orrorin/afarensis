type ExampleT = {
  example: string;
};

type ExampleApiT = {
  id: number;
  text: string;
  date: string;
};

export namespace ExampleType {
  export type Example = ExampleT;
  export type ExampleApi = ExampleApiT;
}
