import ExAxios from '../../../../commons/services/exAxios';

const deleteSignOut = async () => {
  return ExAxios({
    method: 'DELETE',
    url: '/api/v1/public/users/signout',
  });
};

const signOutService = {
  deleteSignOut,
};

export default signOutService;