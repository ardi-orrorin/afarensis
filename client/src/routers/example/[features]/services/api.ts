import axios from 'axios';

const getExample = async ({ params }: { params: { id: number; sort: string } }) => {
  return axios.get('/mocks/example.json');
};

const exampleServiceApi = {
  getExample,
};

export default exampleServiceApi;
