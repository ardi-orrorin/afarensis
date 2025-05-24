import systemSettingQuery from './[features]/stores/query';

const Index = () => {
  const { data: publicData } = systemSettingQuery.publicQuery();
  const { data: privateData } = systemSettingQuery.privateQuery();

  return (
    <div>
      system
    </div>
  );
};

export default Index;




