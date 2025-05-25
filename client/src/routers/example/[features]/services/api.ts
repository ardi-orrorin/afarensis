import axios from 'axios';

const getExample = async () => {
  return axios.get('/mocks/example.json');
};

const getTest = async () => {
  return axios.get('/api/v1/basic/test');
};

const getUsers = async () => {
  return axios.get('/api/v1/public/users');
};

const saveUser = async () => {
  return axios.get('/api/v1/public/users/save');
};

const exampleServiceApi = {
  getExample,
  getTest,
  getUsers,
  saveUser,
};

export default exampleServiceApi;
