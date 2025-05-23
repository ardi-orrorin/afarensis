import { Link } from 'react-router-dom';
import { useCookies } from 'react-cookie';

const LeftNavigator = () => {

  const [cookies] = useCookies(['access_token']);

  return (
    <nav>
      <Link to={`/`}>Home</Link>
      <Link to={`/example`}>Example</Link>

      {
        cookies.access_token
          ? <Link to={`/signout`}>SignOut</Link>
          : <>
            <Link to={`/signup`}>SignUp</Link>
            <Link to={`/signin`}>SignIn</Link>
          </>
      }
    </nav>
  );
};

export default LeftNavigator;